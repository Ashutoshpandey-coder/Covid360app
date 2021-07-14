package covid360rf.covid360.model

data class VaccinationCentre(
        val centerName: String,
        val centerAddress: String,
        val centerFromTime: String,
        val centerToTime: String,
        var fee_type: String,
        var ageLimit: Int,
        var vaccineName: String,
        var availableCapacity: Int
)