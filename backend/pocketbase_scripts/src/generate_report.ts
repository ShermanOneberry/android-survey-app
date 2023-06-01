import PocketBase from 'pocketbase';
import { TformData, Texpand } from "./types/pocketbase-get-types"
import {Collections, SurveyResultsResponse} from "./types/pocketbase-types"
import Excel = require('@siema-team/spreadsheets');
import axios from 'axios';
require('dotenv').config()

const pb = new PocketBase('http://127.0.0.1:8090');

var fileToken = null

function axios_get_image_buffer(url: string) {
    return axios
    .get(url, {
      responseType: 'arraybuffer'
    })
    .then(response => Buffer.from(response.data, 'binary'))
    .catch(function (_) {
        return null
      });
}

async function get_image_from_pocketbase(record, imageRef: string) {
    if (fileToken === null){
        fileToken = await pb.files.getToken()
    }
    let url = pb.files.getUrl(record, imageRef, {'token': fileToken});
    let buffer = await axios_get_image_buffer(url)
    if (buffer !== null) { 
        return buffer
    }
    fileToken = await pb.files.getToken()
    url = pb.files.getUrl(record, imageRef, {'token': fileToken});
    buffer = await axios_get_image_buffer(url)
    if (buffer === null) { 
        return null //TODO: Throw error
    }
    return buffer

}

async function generate_batch_report(batch_num) {
    const records = await pb.collection(Collections.SurveyResults)
    .getFullList<SurveyResultsResponse<TformData, Texpand>>({
        expand: "surveyRequest,assignedUser",
        filter: `surveyRequest.batchNumber=${batch_num}`,
    });
    const workbook = new Excel.Workbook();
    await workbook.xlsx.readFile("./template/Contractor Deployment Plan Batch XXX.xlsx");
    const worksheet = workbook.getWorksheet("Contractor's Deployment Plans");
    if (worksheet === undefined) {
        return //TODO: Throw error
    }
    worksheet.getCell("A2").value = `BATCH NO: ${batch_num}`
    
    records.forEach(record => {
        console.log(record)
        const rowOffset = 5
        const originalRequest = record.expand.surveyRequest
        const rowNum = rowOffset + originalRequest.batchNumber
        const row = worksheet.getRow(rowNum)

        row.getCell("B").value = originalRequest.block
        row.getCell("C").value = originalRequest.streetName
        row.getCell("D").value = originalRequest.area
        row.getCell("E").value = originalRequest.suspectUnit
        row.getCell("F").value = originalRequest.cameraFocusPoint

        row.getCell("G").value = record.expand.assignedUser.name

        //TODO: Clarify what should date/time reference exactly. (Cells H,I)
        const formData = record.formData
        row.getCell("J").value = formData.isFeasible ? "Yes" : "No"
        if (formData.isFeasible) {
            //TODO: Rows K-N
            worksheet.mergeCells(`O${rowNum}:P${rowNum}`)
            row.getCell("O").value = "N/A" //TODO: Check this works
        } else {
            worksheet.mergeCells(`K${rowNum}:N${rowNum}`)
            row.getCell("K").value = "N/A" //TODO: Check this works
            //TODO: Rows O,P
        }
        //const reasonUrl = pb.files.getUrl(record, record.reasonImage, {'token': fileToken});
        row.commit()
    });
    await workbook.xlsx.writeFile(`./generated_reports/Contractor Deployment Plan Batch ${batch_num}.xlsx`);
}
async function main() {
    const authData = await pb.admins.authWithPassword(process.env.ADMIN_USERNAME, process.env.ADMIN_PASSWORD)
    console.log('Authentication successful:', authData);
    generate_batch_report(1)
}

main().catch((error) => {
        console.error('An error occurred while generating the report:', error);
        process.exit(1); 
});;