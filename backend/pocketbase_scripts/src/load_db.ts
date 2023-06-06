import PocketBase from 'pocketbase';
import {Collections, SurveyDetailsRecord} from "./types/pocketbase-types.ts"
import Excel from '@siema-team/spreadsheets';

import dotenv from 'dotenv'
dotenv.config()

const pb = new PocketBase('http://127.0.0.1:8090');
type SurveyDetailsCustomRecord = Required< {id:string} & SurveyDetailsRecord >

async function load_proposed_sites(path: string) {
    const workbook = new Excel.Workbook()
    await workbook.xlsx.readFile(path)
    const worksheet = workbook.getWorksheet("Proposed Sites")

    const batch_regex = /BATCH NO: (\d+)/
    const batch_string = worksheet.getCell("A2").text
    const batchNumber = Number(batch_string.match(batch_regex)[1])

    let current_row = 9
    let batchID_string: string

    while((batchID_string = worksheet.getCell(`A${current_row}`).text).trim() != "") {
        const batchID = Number(batchID_string)
        const surveyDetails : SurveyDetailsCustomRecord = {
            id: `${batchNumber}_${batchID}`.padEnd(15,"_"),
            batchNumber: batchNumber,
            batchID: batchID,
            block: worksheet.getCell(`B${current_row}`).text,
            streetName: worksheet.getCell(`C${current_row}`).text,
            area: worksheet.getCell(`D${current_row}`).text,
            suspectUnit: worksheet.getCell(`G${current_row}`).text,
            cameraFocusPoint: worksheet.getCell(`H${current_row}`).text,
        }
        console.log(surveyDetails)
        await pb.collection(Collections.SurveyDetails).create<SurveyDetailsCustomRecord>(surveyDetails) //TODO: Process this properly

        current_row += 1
    }
}

async function main() { //TODO: Test this
    const authData = await pb.admins.authWithPassword(process.env.ADMIN_USERNAME, process.env.ADMIN_PASSWORD)
    console.log('Authentication successful:', authData);
    await load_proposed_sites("./proposed_sites/Batch 510 Proposed Sites (Oneberry).xlsx")
}

await main().catch((error) => {
        console.error('An error occurred while generating the report:', error);
        process.exit(1); 
})
