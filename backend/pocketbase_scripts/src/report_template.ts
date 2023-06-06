import xl from "excel4node"

export function createExcelBaseTemplate(batch_num: number): [xl.Workbook, xl.Worksheet, xl.Style]  {
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
        }
    }
    const cellStyle = workbook.createStyle(commonStyleData)
    commonStyleData.font.bold = true
    commonStyleData.fill = {
        type: "pattern",
        patternType: "solid",
        fgColor: "F2F2F2"
    }
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

    worksheet.row(5).freeze()
    return [workbook, worksheet, cellStyle]
}