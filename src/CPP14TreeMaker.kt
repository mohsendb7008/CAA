import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTreeProperty

class CPP14TreeMaker: CPP14BaseListener() {

    private val id = ParseTreeProperty<Int>()
    val nodeTypes = ArrayList<String>()
    val nodeChilds = ArrayList<ArrayList<Int>>()
    val nodeCount: Int
    get() = nodeTypes.size

    override fun enterEveryRule(ctx: ParserRuleContext?) {
        super.enterEveryRule(ctx)
        id.put(ctx, nodeCount)
        nodeTypes.add(ctx!!::class.toString())
        nodeChilds.add(ArrayList())
        if(ctx.getParent() != null)
            nodeChilds[id.get(ctx.getParent())].add(id[ctx])
    }

}