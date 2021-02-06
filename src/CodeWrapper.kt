import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import kotlin.math.max
import kotlin.math.min

class CodeWrapper(private val codeText: String, private val codeLines: List<String>,
                  val name: String, val author: String){

    private val tokens = CPP14Lexer(CharStreams.fromString(codeText)).allTokens.map { it.text }
    private val tree = CPP14TreeMaker()

    private var firstCommentType = 0
    private var secondCommentType = 0

    private var firstCurlyBracesType = 0
    private var secondCurlyBracesType = tokens.count { it == "{" }

    private val firstIOType = tokens.count { it == "scanf" || it == "printf" }
    private val secondIOType = tokens.count { it == "cin" || it == "cout" }

    init {
        codeLines.forEach { line ->
            val trim = line.trim()

            if(trim.startsWith("//"))
                firstCommentType++
            else if(trim.contains("//"))
                secondCommentType++

            if(trim.startsWith("{")){
                firstCurlyBracesType++
                secondCurlyBracesType--
            }
        }

        ParseTreeWalker().walk(tree, CPP14Parser(CommonTokenStream(CPP14Lexer(CharStreams.fromString(codeText)))).translationunit())
    }

    val commentPoint: Int
    get() = if(firstCommentType >= secondCommentType) 0 else 1

    val curlyBracesPoint: Int
    get() = if(firstCurlyBracesType >= secondCurlyBracesType) 0 else 1

    val IOPoint: Int
    get() = if(firstIOType >= secondIOType) 0 else 1

    infix fun lexicalDistance(other: CodeWrapper): Double{
        val dp = Array(this.tokens.size){IntArray(other.tokens.size)}
        for(i in this.tokens.indices)
            for(j in other.tokens.indices){
                if(i > 0)
                    dp[i][j] = max(dp[i][j], dp[i-1][j])
                if(j > 0)
                    dp[i][j] = max(dp[i][j], dp[i][j-1])
                if(this.tokens[i] == other.tokens[j])
                    dp[i][j] = max(dp[i][j], 1 + if(i > 0 && j > 0) dp[i-1][j-1] else 0)
            }
        val ans = dp[this.tokens.size-1][other.tokens.size-1]
        return 1 - ans.toDouble() / min(this.tokens.size, other.tokens.size)
    }

    infix fun commonSubtrees(other: CodeWrapper): Int{
        val dp = Array(this.tree.nodeCount){IntArray(other.tree.nodeCount){-1}}
        var commonSubtrees = 0
        for(i in 0 until this.tree.nodeCount)
            for(j in 0 until other.tree.nodeCount)
                if(cdp(dp, i, j, other) == 1)
                    commonSubtrees++
        return commonSubtrees
    }

    private fun cdp(dp: Array<IntArray>, i: Int, j: Int, other: CodeWrapper): Int{
        if(dp[i][j] != -1)
            return dp[i][j]
        if(this.tree.nodeTypes[i] != other.tree.nodeTypes[j]){
            dp[i][j] = 0
            return 0
        }
        if(this.tree.nodeChilds[i].size != other.tree.nodeChilds[j].size){
            dp[i][j] = 0
            return 0
        }
        for(c in 0 until this.tree.nodeChilds[i].size)
            if(cdp(dp, this.tree.nodeChilds[i][c], other.tree.nodeChilds[j][c], other) == 0){
                dp[i][j] = 0
                return 0
            }
        dp[i][j] = 1
        return 1
    }
}