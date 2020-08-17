import mapboxgl, { GeoJSONSourceRaw } from "mapbox-gl";
import mapTilerKey from "./mapTilerKey";
import $, { type } from "jquery";

import { getColorString } from "./colorLib";

const style = getOsmVectorTilesStyle();
const map = new mapboxgl.Map({
    container: "map",
    style: style
});

let registeredCollections: string[] = []

map.on("load", () => {
    addAllPointsAndPolygons(map);
});

function getOsmVectorTilesStyle(): string {
    return `https://api.maptiler.com/maps/ecfe4b94-ad4a-49cc-99c5-8dbf2421fbe5/style.json?key=${mapTilerKey}`;
}

function getOsmRasterTilesStyle(): mapboxgl.Style {
    return {
        "version": 8,
        "sources": {
            "osm": {
                "type": "raster",
                "tiles": [
                    "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
                ],
                "tileSize": 256
            }
        },
        "layers": [
            {
                "id": "osm",
                "type": "raster",
                "source": "osm"
            }
        ]
    };
}



function showPopupPoint(e: any) {
    try {
        var coordinates = e.features[0].geometry.coordinates.slice();
        var description = e.features[0].properties.description;

        // Ensure that if the map is zoomed out such that multiple
        // copies of the feature are visible, the popup appears
        // over the copy being pointed to.
        while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
            coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
        }

        new mapboxgl.Popup()
            .setLngLat(coordinates)
            .setHTML(`<div>${description}</div>`)
            .addTo(map);
    } catch (e) {
        /*Fall through */
    }
}

function showPopupPolygon(e: any) {
    new mapboxgl.Popup()
        .setLngLat(e.lngLat)
        .setHTML(e.features[0].properties.description)
        .addTo(map);
}

function reset() {
    registeredCollections.forEach((source: string) => {

        if (map.getLayer(`PolygonsOf${source}Source`)) {
            map.removeLayer(`PolygonsOf${source}Source`);
            map.off("click", `PolygonsOf${source}Source`, showPopupPolygon);
        }

        if (map.getLayer(`PointsOf${source}Source`)) {
            map.removeLayer(`PointsOf${source}Source`);
            map.off("click", `PointsOf${source}Source`, showPopupPoint);
        }

        map.removeSource(`${source}Source`);

    })
    registeredCollections = []

}

function handleBboxApply() {

    let x1: number = +($("#x1").val() as string);
    let y1: number = +($("#y1").val() as string);
    let x2: number = +($("#x2").val() as string);
    let y2: number = +($("#y2").val() as string);


    if (Number.isNaN(x1) || Number.isNaN(y1) || Number.isNaN(x2) || Number.isNaN(y2)) {
        console.error("Please provide correct bbox parameters");
        return;
    }

    reset();
    let bbox: number[] = [x1, y1, x2, y2]
    addAllPointsAndPolygons(map, bbox);

}

$("#applyButton").click(handleBboxApply);

function addAllPointsAndPolygons(map: mapboxgl.Map, bbox: number[] | undefined = undefined) {
    fetch('http://localhost:8080/collections').then((response: any) => {
        response.json().then((data: any) => {

            let collections: any[] = data.collections;
            let bboxString: string = "";

            if (bbox) {
                bboxString = `&bbox=${bbox[0]},${bbox[1]},${bbox[2]},${bbox[3]}`
            }

            let collectionSelection = $("#collectionSelection")
            collectionSelection.empty();

            collections.forEach(collection => {
                collectionSelection.append(`
                    <label for="cbx${collection.id}">${collection.title}</label>
                    <input id="cbx${collection.id}" type="checkbox" Checked>
                    <br>
                `)

                fetch(`http://localhost:8080/collections/${collection.id}/items?limit=1000` + bboxString).then((response: any) => {
                    response.json().then((data: any) => {
                        map.addSource(`${collection.id}Source`, {
                            type: 'geojson',
                            data: data
                        });

                        registeredCollections.push(collection.id);

                        map.addLayer({
                            "id": `PolygonsOf${collection.id}Source`,
                            'type': 'fill',
                            'source': `${collection.id}Source`,
                            'paint': {
                                'fill-color': getColorString(collection.id),
                                'fill-opacity': 0.4
                            },
                        });
                        map.addLayer({
                            "id": `PointsOf${collection.id}Source`,
                            "type": "circle",
                            "source": `${collection.id}Source`,
                            "paint": {
                                "circle-radius": 5,
                                "circle-color": getColorString(collection.id)
                            }
                        });

                        $(`#cbx${collection.id}`).change((e: any) => {
                            if (e.target.checked) {
                                map.setLayoutProperty(`PointsOf${collection.id}Source`, 'visibility', 'visible')
                                map.setLayoutProperty(`PolygonsOf${collection.id}Source`, 'visibility', 'visible')
                            } else {
                                map.setLayoutProperty(`PointsOf${collection.id}Source`, 'visibility', 'none')
                                map.setLayoutProperty(`PolygonsOf${collection.id}Source`, 'visibility', 'none')
                            }
                        })

                        // When a click event occurs on a feature in the places layer, open a popup at the
                        // location of the feature, with description HTML from its properties.
                        map.on('click', `PointsOf${collection.id}Source`, showPopupPoint);

                        // Change the cursor to a pointer when the mouse is over the places layer.
                        map.on('mouseenter', `PointsOf${collection.id}Source`, function () {
                            map.getCanvas().style.cursor = 'pointer';
                        });

                        // Change it back to a pointer when it leaves.
                        map.on('mouseleave', `PointsOf${collection.id}Source`, function () {
                            map.getCanvas().style.cursor = '';
                        });

                        // When a click event occurs on a feature in the states layer, open a popup at the
                        // location of the click, with description HTML from its properties.
                        map.on('click', `PolygonsOf${collection.id}Source`, showPopupPolygon);

                        // Change the cursor to a pointer when the mouse is over the states layer.
                        map.on('mouseenter', `PolygonsOf${collection.id}Source`, function () {
                            map.getCanvas().style.cursor = 'pointer';
                        });

                        // Change it back to a pointer when it leaves.
                        map.on('mouseleave', `PolygonsOf${collection.id}Source`, function () {
                            map.getCanvas().style.cursor = '';
                        });

                    })
                })
            })
        })
    })
}