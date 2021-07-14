package covid360rf.covid360.model

data class Questiondata(
    var id:Int,
    var question:String,
    var option_one:String,
    var option_two:String,

    var answer:Int,
)