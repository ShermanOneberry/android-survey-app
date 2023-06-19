migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.options = {
    "query": "SELECT \n  surveyDetails.batchNumber as id,\n  MAX(MAX(surveyDetails.updated),MAX(surveyResults.updated)) as updated\nFROM surveyDetails\nINNER JOIN surveyResults on surveyDetails.id == surveyResults.surveyRequest\nGROUP BY batchNumber"
  }

  // remove
  collection.schema.removeField("0g960nju")

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.options = {
    "query": "SELECT \n  (ROW_NUMBER() OVER()) as id, \n  surveyDetails.batchNumber,\n  MAX(MAX(surveyDetails.updated),MAX(surveyResults.updated)) as updated\nFROM surveyDetails\nINNER JOIN surveyResults on surveyDetails.id == surveyResults.surveyRequest\nGROUP BY batchNumber"
  }

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "0g960nju",
    "name": "batchNumber",
    "type": "number",
    "required": false,
    "unique": false,
    "options": {
      "min": null,
      "max": null
    }
  }))

  return dao.saveCollection(collection)
})
