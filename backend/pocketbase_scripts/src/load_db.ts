import PocketBase from 'pocketbase';
import {Collections, SurveyDetailsRecord} from "./types/pocketbase-types.ts"
import Excel from '@siema-team/spreadsheets';
import fs from "fs"
import path from "path"
import dotenv from 'dotenv'
dotenv.config()

const pb = new PocketBase(process.env.POCKETBASE_URL);
type SurveyDetailsCustomRecord = Required< {id:string} & SurveyDetailsRecord >

let folderPath = "proposed_sites"
if (process.argv.length > 2) {
    folderPath = process.argv[2]
}

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
        const checkExistsResponse = await pb.collection(Collections.SurveyDetails).getList(1,1, {filter:`id='${surveyDetails.id}'`})
        if (checkExistsResponse.totalItems != 0) {
            await pb.collection(Collections.SurveyDetails).update<SurveyDetailsRecord>(surveyDetails.id, surveyDetails)
        } else {
            await pb.collection(Collections.SurveyDetails).create<SurveyDetailsCustomRecord>(surveyDetails)
        }
        console.log(`Processed record '${surveyDetails.id}'`)
        current_row += 1
    }
}

function getAllExcelFiles(directoryPath:string) {
    const excelFiles: string[] = [];
  
    // Read the contents of the directory
    const files = fs.readdirSync(directoryPath);
  
    // Iterate through each file
    files.forEach(file => {
      const filePath = path.join(directoryPath, file);
      const fileStat = fs.statSync(filePath);
  
      // Check if the file is a regular file and has .xlsx or .xls extension
      if (fileStat.isFile() && (file.endsWith('.xlsx') || file.endsWith('.xls'))) {
        excelFiles.push(filePath);
      }
  
      // Recursively process subdirectories
      if (fileStat.isDirectory()) {
        excelFiles.push(...getAllExcelFiles(filePath));
      }
    });
  
    return excelFiles;
  }

async function main() {
    await pb.collection(Collections.Bots).authWithPassword(process.env.BOT_USERNAME, process.env.BOT_PASSWORD)
    console.log('Authentication successful');
    for (const excel_path of getAllExcelFiles(folderPath)) {
        console.log(`Processing file: \`${excel_path}\``)
        await load_proposed_sites(excel_path)
        console.log(`Completed file: \`${excel_path}\``)
    }
    
}

await main().catch((error) => {
        console.error('An error occurred while generating the report:', error);
        process.exit(1); 
})
