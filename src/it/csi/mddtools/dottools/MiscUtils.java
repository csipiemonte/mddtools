package it.csi.mddtools.dottools;

import org.eclipse.zest.internal.dot.parser.dot.*;

public class MiscUtils {
	public static GraphType getGraphType(MainGraph graph){
		return graph.getType();
	}
	
	public static void setGraphType(MainGraph graph, GraphType type){
		graph.setType(type);
	}
	
	public static void setOp(EdgeRhsNode node, String op){
		node.setOp(op.equals("DIRECTED") ? EdgeOp.DIRECTED : EdgeOp.UNDIRECTED);
	}
	
	public static void setNodeId(EdgeRhsNode node, NodeId nid){
		node.setNode(nid);
	}
	
	public static NodeId getNodeId(EdgeRhsNode node){
		return node.getNode();
	}
	
	public static NodeId getNodeId(EdgeStmtNode esn) {
		return esn.getNode_id();
	}
	
	public static void setNodeId(EdgeStmtNode node, NodeId nid){
		node.setNode_id(nid);
	}
}
