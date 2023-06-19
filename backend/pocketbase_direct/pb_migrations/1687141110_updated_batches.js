migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.options = {
    "query": "SELECT \n  surveyDetails.batchNumber as id,\n  MAX(MAX(surveyDetails.updated),MAX(surveyResults.updated)) as dataUpdated\nFROM surveyDetails\nINNER JOIN surveyResults on surveyDetails.id == surveyResults.surveyRequest\nGROUP BY batchNumber"
  }

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "1yxqyrb0",
    "name": "dataUpdated",
    "type": "json",
    "required": false,
    "unique": false,
    "options": {}
  }))

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.options = {
    "query": "SELECT \n  surveyDetails.batchNumber as id,\n  MAX(MAX(surveyDetails.updated),MAX(surveyResults.updated)) as updated\nFROM surveyDetails\nINNER JOIN surveyResults on surveyDetails.id == surveyResults.surveyRequest\nGROUP BY batchNumber"
  }

  // remove
  collection.schema.removeField("1yxqyrb0")

  return dao.saveCollection(collection)
})
