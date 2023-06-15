import PocketBase from 'pocketbase';
import { TformData, Texpand } from "./types/pocketbase-get-types.ts"
import {Collections, SurveyResultsResponse} from "./types/pocketbase-types.ts"
import {createExcelBaseTemplate} from "./report_template.ts"
import {format, parse} from "date-fns"
import xl, {Style} from "excel4node"
import fs from "fs"
import axios from 'axios';

import dotenv from 'dotenv'
dotenv.config()

const pb = new PocketBase(process.env.POCKETBASE_URL);


async function axios_get_image_buffer(url: string):Promise<Buffer|null> {
    try {
        const response = await axios
            .get(url, {
                responseType: 'arraybuffer'
            });
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
        return response.data;
    } catch {
        return null;
    }
}

let fileToken:string = null
async function get_image_from_pocketbase(
    record: SurveyResultsResponse<TformData, Texpand>, imageRef: string) {
    if (fileToken == null){
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
        throw Error(`Unable to get image from '${imageRef}'`)
    }
    return buffer

}
function generateLocationDescription(report: TformData, defaultStyle: Style): (string|(Style["font"]&{value?:string}))[] {
    const nearbyLocationText = 
    report.nearbyDescription.trim().length == 0 ? "" : " " + report.nearbyDescription.trim()
    const distanceNumber =
    report.locationDistance.substring(0, report.locationDistance.length - 1).trim()
    const generalLocationDescriptionPartial =
            `${report.blockLocation.trim()} ${report.streetLocation.trim()}` +
            `${nearbyLocationText}. Distance: `
    let formatFirstPart:string
    switch(report.locationType) {
        case "CORRIDOR":
            formatFirstPart =  `Deploy at level ${report.corridorLevel.trim()} ` +
                    `common corridor of ${generalLocationDescriptionPartial}`
            break
        case "STAIRWAY":
            {
                const lowerLevel: string = report.stairwayLowerLevel.trim()
                const upperLevel: string = (parseInt(lowerLevel) + 1).toString()
                formatFirstPart =  "Deploy at staircase landing between " +
                    `level ${lowerLevel} and ${upperLevel} of ${generalLocationDescriptionPartial}`
                break
            }
        case "GROUND":
            {
                const groundTypeFragment = {
                    "VOID_DECK": "void deck ",
                    "GRASS_PATCH": "grass patch ",
                    "OTHER": "",
                }[report.groundType]
                formatFirstPart = `Deploy at ground level ${groundTypeFragment}of ${generalLocationDescriptionPartial}`
                break
            }
        case "MULTISTORYCARPARK":
            formatFirstPart = `Deploy at MSCP level ${report.carparkLevel} of ${generalLocationDescriptionPartial}`
            break
        case "ROOF":
            formatFirstPart = `Deploy at roof of ${generalLocationDescriptionPartial}`
            break
    }
    const returnValue = [
        formatFirstPart,
        {bold: true, value: distanceNumber.toString(), ...defaultStyle.font},
        {bold: false, value: " meters away", ...defaultStyle.font},
    ]
    return returnValue
}

const CM_IN_EMU = 360000
const MARGIN_IN_EMU = CM_IN_EMU / 3

async function processSingleRecord(
    template: [xl.Workbook, xl.Worksheet, xl.Style], 
    record: SurveyResultsResponse<TformData, Texpand>
) {
    const [/* workbook */ , worksheet, cellStyle] = template
    const rowOffset = 5
    const originalRequest = record.expand.surveyRequest
    const rowNum = rowOffset + originalRequest.batchNumber

    worksheet.row(rowNum).setHeight(249.75)

    worksheet.cell(rowNum, 1).number(originalRequest.batchNumber).style(cellStyle)
    worksheet.cell(rowNum, 2).string(originalRequest.block).style(cellStyle).style({font: {bold: true}})
    worksheet.cell(rowNum, 3).string(originalRequest.streetName).style(cellStyle).style(
        {font: {bold: true}, alignment: {horizontal: "left"}}
    )
    worksheet.cell(rowNum, 4).string(originalRequest.area).style(cellStyle)
    worksheet.cell(rowNum, 5).string(originalRequest.suspectUnit).style(cellStyle)
    worksheet.cell(rowNum, 6).string(originalRequest.cameraFocusPoint).style(cellStyle)

    worksheet.cell(rowNum, 7).string(record.expand.assignedUser.name).style(cellStyle)
    const formData = record.formData

    worksheet.cell(rowNum, 8).style(cellStyle).string(
        format(new Date(formData.surveyDate), "d/M/yyyy")
    )
    worksheet.cell(rowNum, 9).style(cellStyle).string(
        format(
            parse(formData.surveyTime, "HH:mm:ss.SSS", new Date(formData.surveyDate)),
            "HH:mm 'hrs'"
        )
    )
    worksheet.cell(rowNum, 10).string(formData.isFeasible ? "Yes" : "No").style(cellStyle)

    const reasonImageBuffer = await get_image_from_pocketbase(record, record.reasonImage)

    if (formData.isFeasible) {
        worksheet.cell(rowNum, 11).string(formData.boxCount).style(cellStyle)
        worksheet.cell(rowNum, 12).string(formData.cameraCount).style(cellStyle)
        worksheet.cell(rowNum, 13)
            .style(cellStyle)
            .style({alignment:{horizontal:"left"}})
            .string(generateLocationDescription(formData, cellStyle))
        worksheet.cell(rowNum, 14).style(cellStyle)
        worksheet.addImage({
            image: reasonImageBuffer,
            type: 'picture',
            position: {
              type: 'twoCellAnchor',
              from: {
                col: 14,
                colOff: MARGIN_IN_EMU,
                row: rowNum,
                rowOff: MARGIN_IN_EMU,
              },
              to: {
                col: 15,
                colOff: -MARGIN_IN_EMU,
                row: rowNum+1,
                rowOff: -MARGIN_IN_EMU,
              },
            },
          });
        worksheet.cell(rowNum, 15, rowNum, 16, true).string("N/A").style(cellStyle)
    } else {
        worksheet.cell(rowNum, 11,rowNum,14,true).string("N/A").style(cellStyle).style({
            alignment: {
                horizontal: "left",
                vertical: "top",
            }
        })
        worksheet.cell(rowNum, 15).string(formData.nonFeasibleExplanation)
        worksheet.addImage({
            image: reasonImageBuffer,
            type: 'picture',
            position: {
              type: 'twoCellAnchor',
              from: {
                col: 15,
                colOff: MARGIN_IN_EMU,
                row: rowNum,
                rowOff: CM_IN_EMU * 2,
              },
              to: {
                col: 16,
                colOff: -MARGIN_IN_EMU,
                row: rowNum+1,
                rowOff: -MARGIN_IN_EMU,
              },
            },
          });
          worksheet.cell(rowNum, 16).string("N/A").style(cellStyle)
    }

    if (formData.hasAdditionalNotes) {
        worksheet.cell(rowNum, 17).string(formData.techniciansNotes).style(cellStyle).style({
            alignment: {
                horizontal: "left",
                vertical: "top",
            }
        })
        const additionalImage = await get_image_from_pocketbase(record, record.additionalImage)
        worksheet.addImage({
            image: additionalImage,
            type: 'picture',
            position: {
              type: 'twoCellAnchor',
              from: {
                col: 17,
                colOff: MARGIN_IN_EMU,
                row: rowNum,
                rowOff: CM_IN_EMU * 2,
              },
              to: {
                col: 18,
                colOff: -MARGIN_IN_EMU,
                row: rowNum+1,
                rowOff: -MARGIN_IN_EMU,
              },
            },
          });
    } else {
        worksheet.cell(rowNum, 17).string("N/A").style(cellStyle)
    }
    worksheet.cell(rowNum, 18).string("N/A").style(cellStyle)
    console.log(`Generated row ${originalRequest.batchNumber}`)
}

async function generate_batch_report(batch_num: number){
    const records = await pb.collection(Collections.SurveyResults)
    .getFullList<SurveyResultsResponse<TformData, Texpand>>({
        expand: "surveyRequest,assignedUser",
        filter: `surveyRequest.batchNumber=${batch_num}`,
    });
    const template = createExcelBaseTemplate(batch_num)
    const workbook = template[0]
    
    for (const record of records){
        await processSingleRecord(template, record)
    }
    const buffer = await workbook.writeToBuffer();
    fs.writeFileSync(`./generated_reports/Contractor Deployment Plan Batch ${batch_num}.xlsx`, buffer);
}
async function main() {
    await pb.collection(Collections.Bots).authWithPassword(process.env.BOT_USERNAME, process.env.BOT_PASSWORD)
    console.log('Authentication successful');
    const batch_num = 1
    await generate_batch_report(batch_num)
    console.log(`Completed report generation for batch ${batch_num}`)
}

await main().catch((error) => {
        console.error('An error occurred while generating the report:', error);
        process.exit(1); 
})