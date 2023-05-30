const PocketBase = require('pocketbase/cjs')
var xl = require('excel4node');
require('dotenv').config()

const pb = new PocketBase('http://127.0.0.1:8090');

async function generate_batch_report(batch_num, fileToken) {
    const records = await pb.collection('surveyResults').getFullList({
        expand: "surveyRequest",
        filter: `surveyRequest.batchNumber=${batch_num}`,
    });
    var wb = new xl.Workbook();
    var ws = wb.addWorksheet("Contractor's Deployment Plans");
    
    records.array.forEach(record => {
        //const reasonUrl = pb.files.getUrl(record, record.reasonImage, {'token': fileToken});
    });
}
async function main() {
    const authData = await pb.admins.authWithPassword(process.env.ADMIN_USERNAME, process.env.ADMIN_PASSWORD)
    console.log('Authentication successful:', authData);
    const fileToken = pb.files.getToken()
    generate_batch_report(1, fileToken)
}

main().catch((error) => {
        console.error('An error occurred while generating the report:', error);
        process.exit(1); 
});;