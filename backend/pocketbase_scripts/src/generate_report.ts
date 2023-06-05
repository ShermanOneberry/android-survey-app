import PocketBase from 'pocketbase';
import { TformData, Texpand } from "./types/pocketbase-get-types.ts"
import {Collections, SurveyResultsResponse} from "./types/pocketbase-types.ts"
import xl from "excel4node"
import fs from "fs"
import axios from 'axios';

import dotenv from 'dotenv'
dotenv.config()

const pb = new PocketBase('http://127.0.0.1:8090');


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
            {
                const lowerLevel: string = report.stairwayLowerLevel.trim()
                const upperLevel: string = (parseInt(lowerLevel) + 1).toString()
                return "Deploy at staircase landing between " +
                    `level ${lowerLevel} and ${upperLevel} of ${generalLocationDescription}`
            }
        case "GROUND":
            {
                const groundTypeFragment = {
                    "VOID_DECK": "void deck ",
                    "GRASS_PATCH": "grass patch ",
                    "OTHER": "",
                }[report.groundType]
                return `Deploy at ground level ${groundTypeFragment}of ${generalLocationDescription}`
            }
        case "MULTISTORYCARPARK":
            return `Deploy at MSCP level ${report.carparkLevel} of ${generalLocationDescription}`
        case "ROOF":
            return "Deploy at roof of $generalLocationDescription" 
    }
}
function createExcelBaseTemplate(batch_num: number): [xl.Workbook, xl.Worksheet, xl.Style]  {
    const workbook = new xl.Workbook();
    const worksheet = workbook.addWorksheet(
        "Contractor's Deployment Plans",
        {
            sheetView: {
                zoomScale: 60
            }
        });

    worksheet.cell(1,1).string("CONTRACTOR'S DEPLOYMENT PLAN")
        .style({
            font: {
                size: 16,
                bold: true,
                name: "Times New Roman"
            },
        })
    worksheet.cell(2,1).string(`BATCH NO: ${batch_num}`)
        .style({
            font: {
                size: 16,
                name: "Times New Roman"
            },
        })
    const commonStyleData: xl.Style = {
        font: {
            size: 16,
            name: "Times New Roman",
        },
        alignment: {
            vertical: "center",
            horizontal: "center",
            wrapText: true
        },
        border: {
            left: {
                style: "thin"
            },
            right: {
                style: "thin"
            },
            top: {
                style: "thin"
            },
            bottom: {
                style: "thin"
            },
        },
        fill: {
            type: "pattern",
            patternType: "solid",
            fgColor: "F2F2F2"
        }
    }
    const cellStyle = workbook.createStyle(commonStyleData)
    commonStyleData.font.bold = true
    const headerStyle = workbook.createStyle(commonStyleData)

    worksheet.row(5).setHeight(81.6)

    worksheet.cell(5,1).string("No").style(headerStyle)
    worksheet.column(1).setWidth(4.67)
    worksheet.cell(5,2).string("Block").style(headerStyle)
    worksheet.column(2).setWidth(12.89)
    worksheet.cell(5,3).string("Street Name").style(headerStyle)
    worksheet.column(3).setWidth(34.67)
    worksheet.cell(5,4).string("Area").style(headerStyle)
    worksheet.column(4).setWidth(14.89)
    worksheet.cell(5,5).string("Suspect Unit(s)").style(headerStyle)
    worksheet.column(5).setWidth(24.89)
    worksheet.cell(5,6).string("Camera Focus Point").style(headerStyle)
    worksheet.column(6).setWidth(24.33)

    worksheet.cell(4,7,4,10, true).string("ASSESSMENT").style(headerStyle)

    worksheet.cell(5,7).string("Team Assignment").style(headerStyle)
    worksheet.column(7).setWidth(15.56)
    worksheet.cell(5,8).string("Date").style(headerStyle)
    worksheet.column(8).setWidth(13.67)
    worksheet.cell(5,9).string("Time/hrs").style(headerStyle)
    worksheet.column(9).setWidth(11.78)
    worksheet.cell(5,10).string("Feasible\n(Yes/No)").style(headerStyle)
    worksheet.column(10).setWidth(12.22)

    worksheet.cell(4,11,4,14, true).string("FEASIBLE TO DEPLOY").style(headerStyle)

    worksheet.cell(5,11).string("No. of Boxes").style(headerStyle)
    worksheet.column(11).setWidth(9.33)
    worksheet.cell(5,12).string("No. of Cameras").style(headerStyle)
    worksheet.column(12).setWidth(13.11)
    worksheet.cell(5,13).string("Description of Planned Deployment Location").style(headerStyle)
    worksheet.column(13).setWidth(29.22)
    worksheet.cell(5,14).string("Photo of Planned Deployment Location").style(headerStyle)
    worksheet.column(14).setWidth(29.22)

    worksheet.cell(4,15,4,16, true).string("NOT FEASIBLE TO DEPLOY").style(headerStyle)

    worksheet.cell(5,15).string("Reason").style(headerStyle)
    worksheet.column(15).setWidth(31.67)
    worksheet.cell(5,16).string("Suggested Solutions/suggested locations (another option)").style(headerStyle)
    worksheet.column(16).setWidth(27.11)

    worksheet.cell(5,17).string("Technician's Note").style(headerStyle)
    worksheet.column(17).setWidth(34.33)
    worksheet.cell(5,18).string("Site Survey Conclusion").style(headerStyle)
    worksheet.column(18).setWidth(16.67)
    return [workbook, worksheet, cellStyle]
}

async function generate_batch_report(batch_num: number){
    const records = await pb.collection(Collections.SurveyResults)
    .getFullList<SurveyResultsResponse<TformData, Texpand>>({
        expand: "surveyRequest,assignedUser",
        filter: `surveyRequest.batchNumber=${batch_num}`,
    });
    const [workbook, worksheet, cellStyle] = createExcelBaseTemplate(batch_num)
    
    for (const record of records){
        console.log(record)
        const rowOffset = 5
        const originalRequest = record.expand.surveyRequest
        const rowNum = rowOffset + originalRequest.batchNumber

        worksheet.row(rowNum).setHeight(249.75)

        worksheet.cell(rowNum, 1).number(originalRequest.batchNumber).style(cellStyle)
        worksheet.cell(rowNum, 2).string(originalRequest.block).style(cellStyle).style({font: {bold: true}})
        worksheet.cell(rowNum, 3).string(originalRequest.streetName).style(cellStyle).style({font: {bold: true}})
        worksheet.cell(rowNum, 4).string(originalRequest.area).style(cellStyle)
        worksheet.cell(rowNum, 5).string(originalRequest.suspectUnit).style(cellStyle)
        worksheet.cell(rowNum, 6).string(originalRequest.cameraFocusPoint).style(cellStyle)

        worksheet.cell(rowNum, 7).string(record.expand.assignedUser.name).style(cellStyle)
        const formData = record.formData

        worksheet.cell(rowNum, 8).string(formData.surveyDate).style(cellStyle) //TODO: Make formatting match reference report
        worksheet.cell(rowNum, 9).string(formData.surveyTime).style(cellStyle) //TODO: Make formatting match reference report
        worksheet.cell(rowNum, 10).string(formData.isFeasible ? "Yes" : "No").style(cellStyle)

        const reasonImageBuffer = await get_image_from_pocketbase(record, record.reasonImage)

        if (formData.isFeasible) {
            worksheet.cell(rowNum, 11).string(formData.boxCount).style(cellStyle)
            worksheet.cell(rowNum, 12).string(formData.cameraCount).style(cellStyle)
            worksheet.cell(rowNum, 13).string(generateLocationDescription(formData)).style(cellStyle)
            //TODO: Add image
            worksheet.cell(rowNum, 15, rowNum, 16, true).string("N/A").style(cellStyle)//TODO: Check this works
        } else {
            worksheet.cell(rowNum,11,rowNum,14,true).string("N/A").style(cellStyle) //TODO: Check this works
            //TODO: column 15,16
        }
        //const reasonUrl = pb.files.getUrl(record, record.reasonImage, {'token': fileToken});
        //row.commit()
    }
    const buffer = await workbook.writeToBuffer();
    fs.writeFileSync(`./generated_reports/Contractor Deployment Plan Batch ${batch_num}.xlsx`, buffer);
}
async function main() {
    const authData = await pb.admins.authWithPassword(process.env.ADMIN_USERNAME, process.env.ADMIN_PASSWORD)
    console.log('Authentication successful:', authData);
    await generate_batch_report(1)
}

await main().catch((error) => {
        console.error('An error occurred while generating the report:', error);
        process.exit(1); 
})