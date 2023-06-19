migrate((db) => {
  const collection = new Collection({
    "id": "wvpld86o2bqxwsf",
    "created": "2023-06-16 10:37:00.136Z",
    "updated": "2023-06-16 10:37:00.136Z",
    "name": "latest_batch_changes",
    "type": "view",
    "system": false,
    "schema": [
      {
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
      }
    ],
    "indexes": [],
    "listRule": null,
    "viewRule": null,
    "createRule": null,
    "updateRule": null,
    "deleteRule": null,
    "options": {
      "query": "SELECT (ROW_NUMBER() OVER()) as id, surveyDetails.batchNumber\nFROM surveyDetails\nGROUP BY batchNumber"
    }
  });

  return Dao(db).saveCollection(collection);
}, (db) => {
  const dao = new Dao(db);
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf");

  return dao.deleteCollection(collection);
})
