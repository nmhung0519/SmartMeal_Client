package android.example.smartmeal.table

class TableModel{
    var id: Int = 0
    var tableName: String = ""
    var status: Int = 0
    var isActive: Int = 0

    constructor(id: Int, tableName: String) {
        this.id = id
        this.tableName = tableName
    }

}