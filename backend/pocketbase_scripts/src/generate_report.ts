import PocketBase from 'pocketbase';
import { TformData, Texpand } from "./types/pocketbase-get-types.ts"
import {Collections, SurveyResultsResponse} from "./types/pocketbase-types.ts"
import Excel from 'exceljs';
import axios from 'axios';

import dotenv from 'dotenv'
dotenv.config()

const pb = new PocketBase('http://127.0.0.1:8090');

var fileToken = null

function axios_get_image_buffer(url: string):Promise<Buffer|null> {
    return axios
    .get(url, {
      responseType: 'arraybuffer'
    })
    .then(response => Buffer.from(response.data, 'binary'))
    .catch(function (_) {
        return null
      });
}

async function get_image_from_pocketbase(
    record: SurveyResultsResponse<TformData, Texpand>, imageRef: string) {
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
function generateLocationDescription(report: TformData): string {
    const nearbyLocationText = 
    report.nearbyDescription.trim().length == 0 ? "" : " " + report.nearbyDescription.trim()
    const distanceNumber =
    report.locationDistance.substring(0, report.locationDistance.length - 1).trim()
    const generalLocationDescription =
            `${report.blockLocation.trim()} ${report.streetLocation.trim()}` +
            `${nearbyLocationText}. Distance: ${distanceNumber} meters away`
    switch(report.locationType) {
        case "CORRIDOR":
            return `Deploy at level ${report.corridorLevel.trim()} ` +
                    `common corridor of ${generalLocationDescription}`
        case "STAIRWAY":
            const lowerLevel: string = report.stairwayLowerLevel.trim()
            const upperLevel: string = (parseInt(lowerLevel) + 1).toString()
            return "Deploy at staircase landing between " +
                `level ${lowerLevel} and ${upperLevel} of ${generalLocationDescription}`
        case "GROUND":
            const groundTypeFragment = {
                "VOID_DECK": "void deck ",
                "GRASS_PATCH": "grass patch ",
                "OTHER": "",
            }[report.groundType]
            return `Deploy at ground level ${groundTypeFragment}of ${generalLocationDescription}`
        case "MULTISTORYCARPARK":
            return `Deploy at MSCP level ${report.carparkLevel} of ${generalLocationDescription}`
        case "ROOF":
            return "Deploy at roof of $generalLocationDescription" 
    }
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
    
    records.forEach(async record => {
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
        const formData = record.formData

        row.getCell("H").value = formData.surveyDate //TODO: Make formatting match reference report
        row.getCell("I").value = formData.surveyTime //TODO: Make formatting match reference report
        row.getCell("J").value = formData.isFeasible ? "Yes" : "No"

        const reasonImageBuffer = await get_image_from_pocketbase(record, record.reasonImage)
        const reasonImageID = workbook.addImage({ //TODO: Figure out why this breaks value assignment of cells
            buffer: reasonImageBuffer,
            extension: "jpeg", //Assumption of phone picture format
        });
        if (formData.isFeasible) {
            row.getCell("K").value = formData.boxCount
            row.getCell("L").value = formData.cameraCount
            row.getCell("M").value = generateLocationDescription(formData)
            console.log(row.getCell("M").value)
            worksheet.addImage(reasonImageID, `N${rowNum}:N${rowNum}`)
            worksheet.mergeCells(`O${rowNum}:P${rowNum}`)
            row.getCell("O").value = "N/A" //TODO: Check this works
            console.log("TESTING TESTING 123")
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