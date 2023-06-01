import {UsersResponse, SurveyDetailsResponse} from "./pocketbase-types.ts"

export type TformData = {
    blockLocation: string
    boxCount: string
    cameraCount: string
    carparkLevel: string
    corridorLevel: string
    groundType: "VOID_DECK" | "GRASS_PATCH" | "OTHER"
    hasAdditionalNotes: boolean
    isFeasible: boolean
    locationDistance: string
    locationType: "CORRIDOR" | "STAIRWAY" | "GROUND" | "MULTISTORYCARPARK" | "ROOF"
    nearbyDescription: string
    nonFeasibleExplanation: string
    stairwayLowerLevel: string
    streetLocation: string
    surveyDate: string
    surveyTime: string
    techniciansNotes: string
}
export type Texpand = {
    assignedUser: UsersResponse
    surveyRequest: SurveyDetailsResponse
}
