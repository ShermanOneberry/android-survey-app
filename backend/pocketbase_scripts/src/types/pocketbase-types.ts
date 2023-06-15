/**
* This file was @generated using pocketbase-typegen
*/

export enum Collections {
	Bots = "bots",
	SurveyDetails = "surveyDetails",
	SurveyResults = "surveyResults",
	Users = "users",
}

// Alias types for improved usability
export type IsoDateString = string
export type RecordIdString = string
export type HTMLString = string

// System fields
export type BaseSystemFields<T = never> = {
	id: RecordIdString
	created: IsoDateString
	updated: IsoDateString
	collectionId: string
	collectionName: Collections
	expand?: T
}

export type AuthSystemFields<T = never> = {
	email: string
	emailVisibility: boolean
	username: string
	verified: boolean
} & BaseSystemFields<T>

// Record types for each collection

export type BotsRecord = never

export type SurveyDetailsRecord = {
	batchNumber?: number
	batchID?: number
	block?: string
	streetName?: string
	area?: string
	suspectUnit?: string
	cameraFocusPoint?: string
}

export type SurveyResultsRecord<TformData = unknown> = {
	surveyRequest: RecordIdString
	assignedUser: RecordIdString
	formData: null | TformData
	reasonImage: string
	additionalImage?: string
}

export type UsersRecord = {
	name?: string
	avatar?: string
}

// Response types include system fields and match responses from the PocketBase API
export type BotsResponse = Required<BotsRecord> & AuthSystemFields
export type SurveyDetailsResponse = Required<SurveyDetailsRecord> & BaseSystemFields
export type SurveyResultsResponse<TformData = unknown, Texpand = unknown> = Required<SurveyResultsRecord<TformData>> & BaseSystemFields<Texpand>
export type UsersResponse = Required<UsersRecord> & AuthSystemFields

// Types containing all Records and Responses, useful for creating typing helper functions

export type CollectionRecords = {
	bots: BotsRecord
	surveyDetails: SurveyDetailsRecord
	surveyResults: SurveyResultsRecord
	users: UsersRecord
}

export type CollectionResponses = {
	bots: BotsResponse
	surveyDetails: SurveyDetailsResponse
	surveyResults: SurveyResultsResponse
	users: UsersResponse
}