package android.example.smartmeal.table

class TableItem {
    var id: Int = 0
    var name: String
    var statusId: Int = 0
    constructor(id: Int, name: String) {
        this.id = id
        this.name = name
    }
    constructor(model: TableModel) {
        id = model.id
        name = model.tableName
        statusId = model.status
    }
}