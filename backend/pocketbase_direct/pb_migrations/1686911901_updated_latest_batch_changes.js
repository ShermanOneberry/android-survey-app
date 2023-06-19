migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.options = {
    "query": "SELECT \n  (ROW_NUMBER() OVER()) as id, \n  surveyDetails.batchNumber,\n  surveyDetails.updated\nFROM surveyDetails\nGROUP BY batchNumber"
  }

  // remove
  collection.schema.removeField("vkvfe075")

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

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.options = {
    "query": "SELECT (ROW_NUMBER() OVER()) as id, surveyDetails.batchNumber\nFROM surveyDetails\nGROUP BY batchNumber"
  }

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "vkvfe075",
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
  collection.schema.removeField("qvx0ebdk")

  return dao.saveCollection(collection)
})
