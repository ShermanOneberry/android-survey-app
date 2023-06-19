migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.options = {
    "query": "SELECT \n  surveyDetails.batchNumber as id,\n  MAX(MAX(surveyDetails.updated),MAX(surveyResults.updated)) as data_updated\nFROM surveyDetails\nINNER JOIN surveyResults on surveyDetails.id == surveyResults.surveyRequest\nGROUP BY batchNumber"
  }

  // remove
  collection.schema.removeField("os4dkrcv")

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "iquvzdru",
    "name": "data_updated",
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
    "query": "SELECT \n  surveyDetails.batchNumber as id,\n  COUNT(surveyResults.id) as surveysCollected,\n  MAX(MAX(surveyDetails.updated),MAX(surveyResults.updated)) as updated,\n  \"\" as created\nFROM surveyDetails\nINNER JOIN surveyResults on surveyDetails.id == surveyResults.surveyRequest\nGROUP BY batchNumber"
  }

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "os4dkrcv",
    "name": "surveysCollected",
    "type": "number",
    "required": false,
    "unique": false,
    "options": {
      "min": null,
      "max": null
    }
  }))

  // remove
  collection.schema.removeField("iquvzdru")

  return dao.saveCollection(collection)
})
