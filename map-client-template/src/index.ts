import mapboxgl, { GeoJSONSourceRaw } from "mapbox-gl";
import mapTilerKey from "./mapTilerKey";
import $ from "jquery"

const style = getOsmVectorTilesStyle();
const map = new mapboxgl.Map({
    container: "map",
    style: style
});

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

function getColorString(collectionId: string) {
    switch (collectionId) {
        case "Accessories":
            return "#9CF6F7"
        case "Battery":
            return "#52FD07"
        case "Cameras":
            return "#A7ADA5"
        case "Connectors":
            return "#3A4935"
        case "Display":
            return "#749FDE"
        case "Electromechanical":
            return "#407C47"
        case "FlexiblePrintedCircuits":
            return "#263027"
        case "Gold":
            return "#F6FA04"
        case "Packaging":
            return "#98420A"
        case "Passives":
            return "#F7A100"
        case "PhoneAssembly":
            return "#0013F7"
        case "Plastics":
            return "#E1E2E8"
        case "PrintedCircuitBoard":
            return "#0A953D"
        case "Semiconductors":
            return "#013113"
        case "Shields":
            return "#3B08D9"
        case "SolderingPaste":
            return "#44557E"
        case "Tantalum":
            return "#868B98"
        case "Tin":
            return "#4E5462"
        case "Tungsten":
            return "#560825"
        default:
            return "#82DCE5"

    }
}

function addAllPointsAndPolygons(map: mapboxgl.Map) {

    fetch('http://localhost:8080/collections').then((response: any) => {

        response.json().then((data: any) => {

            let collections: any[] = data.collections;


            collections.forEach(collection => {

                let selectionArea = $("#selectionArea")

                selectionArea.append(`
                    <label for="cbx${collection.id}">${collection.title}</label>
                    <input id="cbx${collection.id}" type="checkbox" Checked>
                    <br>
                `)

                fetch(`http://localhost:8080/collections/${collection.id}/items?limit=1000`).then((response: any) => {
                    response.json().then((data: any) => {
                        map.addSource(`${collection.id}Source`, {
                            type: 'geojson',
                            data: data
                        });
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
                        map.on('click', `PointsOf${collection.id}Source`, function (e: any) {
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
                        });

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
                        map.on('click', `PolygonsOf${collection.id}Source`, function (e: any) {
                            new mapboxgl.Popup()
                                .setLngLat(e.lngLat)
                                .setHTML(e.features[0].properties.description)
                                .addTo(map);
                        });

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