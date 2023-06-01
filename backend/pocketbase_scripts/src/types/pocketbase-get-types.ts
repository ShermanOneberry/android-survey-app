import {UsersResponse, SurveyDetailsResponse} from "./pocketbase-types"
export type TformData = {
    blockLocation: string
    boxCount: string
    cameraCount: string
    carparkLevel: string
    corridorLevel: string
    groundType: string
    hasAdditionalNotes: boolean
    isFeasible: boolean
    locationDistance: string
    locationType: string
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
