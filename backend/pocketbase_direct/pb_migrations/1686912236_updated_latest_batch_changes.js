migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.options = {
    "query": "SELECT \n  (ROW_NUMBER() OVER()) as id, \n  surveyDetails.batchNumber,\n  MAX(MAX(surveyDetails.updated),MAX(surveyResults.updated)) as updated\nFROM surveyDetails\nINNER JOIN surveyResults on surveyDetails.id == surveyResults.surveyRequest\nGROUP BY batchNumber"
  }

  // remove
  collection.schema.removeField("qvx0ebdk")

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "eirzyvtr",
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
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.options = {
    "query": "SELECT \n  (ROW_NUMBER() OVER()) as id, \n  surveyDetails.batchNumber,\n  surveyDetails.updated\nFROM surveyDetails\nGROUP BY batchNumber"
  }

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "qvx0ebdk",
    "name": "batchNumber",
    "type": "number",
    "required": false,
    "unique": false,
    "options": {
      "min": null,
      "max": null
    }
  }))

  // remove
  collection.schema.removeField("eirzyvtr")

  return dao.saveCollection(collection)
})
