import org.antlr.v4.runtime.CharStreams
import java.io.File
import kotlin.math.log

val stopWords = listOf("#include", "using", "namespace", ";", "{", "}", "(", ")")
val tokenSet = HashSet<String>()

class Document(private val codeText: String, val name: String, val author: String){

    private val tokens = CPP14Lexer(CharStreams.fromString(codeText)).allTokens.map { it.text }
    val tf = tokens.filter { it !in stopWords } .groupingBy { it }.eachCount()
    fun containsToken(token: String): Boolean = token in tf.keys

    init {
        tokenSet.addAll(tf.keys)
    }

}

val documents = ArrayList<Document>()

fun df(token: String) = documents.count { it.containsToken(token) }
fun idf(token: String) = log(documents.size.toDouble() / df(token), 2.0) + 1

fun tfidf(token: String, document: Document) = document.tf.getOrElse(token, { 0 }) * idf(token)

fun main() {
    val datasetFolder = File("dataset")
    datasetFolder.list()!!.forEach{ author ->
        val authorFolder = File("dataset/$author")
        authorFolder.list()!!.forEach { code ->
            val codeFile = File("dataset/$author/$code")
            documents.add(Document(codeFile.readText(), code, author))
        }
    }
    documents.forEach { document ->
        print("${document.author}/${document.name} = ")
        println(tokenSet.map { token -> tfidf(token, document) }
                .joinToString(prefix = "(", postfix = ")", separator = ", "))
    }
}

