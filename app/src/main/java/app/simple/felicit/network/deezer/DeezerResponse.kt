package app.simple.felicit.network.deezer

data class DeezerResponse(
    val data: List<Data>,
    @Transient val next: String,
    @Transient val total: Int
)