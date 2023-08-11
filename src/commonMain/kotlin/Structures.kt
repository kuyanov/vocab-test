import kotlinx.serialization.Serializable

@Serializable
data class Meta(val total: Int, val levels: Map<String, Int>)
