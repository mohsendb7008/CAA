import org.antlr.v4.gui.TestRig
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

val wrappers = ArrayList<CodeWrapper>()
val distances = HashMap<Pair<Int, Int>, MutableList<Double>>()

fun main() {
    // TestRig.main(arrayOf("CPP14", "translationunit", "-gui", "dataset/mohsen/fact.cpp"))
    val datasetFolder = File("dataset")
    datasetFolder.list()!!.forEach{ author ->
        val authorFolder = File("dataset/$author")
        authorFolder.list()!!.forEach { code ->
            val codeFile = File("dataset/$author/$code")
            wrappers.add(CodeWrapper(codeFile.readText(), codeFile.readLines(), code, author))
        }
    }
    var maxCommonSubtrees = 0.0
    for(i in wrappers.indices)
        for(j in i+1 until wrappers.size){
            distances[i to j] = mutableListOf(
                abs(wrappers[i].commentPoint - wrappers[j].commentPoint).toDouble(),
                abs(wrappers[i].curlyBracesPoint - wrappers[j].curlyBracesPoint).toDouble(),
                abs(wrappers[i].IOPoint - wrappers[j].IOPoint).toDouble(),
                wrappers[i] lexicalDistance wrappers[j],
                (wrappers[i] commonSubtrees wrappers[j]).toDouble().also {
                    maxCommonSubtrees = max(maxCommonSubtrees, it)
                }
            )
        }
    distances.forEach { (t, u) ->
        u[u.size-1] = 1.0 - u.last() / maxCommonSubtrees
        val distance = sqrt(u.fold(0.0, {acc, d -> acc + d * d}))
        val c1 = wrappers[t.first]
        val c2 = wrappers[t.second]
        println("distance(${c1.author}/${c1.name}, ${c2.author}/${c2.name}) = $distance")
    }
}