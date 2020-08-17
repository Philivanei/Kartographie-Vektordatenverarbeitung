export function getColorString(collectionId: string) {
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