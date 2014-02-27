package org.logicail.rsbot.scripts.framework.context.providers.walking;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.wrappers.Tile;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 27/02/14
 * Time: 18:18
 */
class IWeb extends MethodProvider {
	private HashMap<Integer, Link> links = new HashMap<Integer, Link>();
	private HashMap<Integer, TileNode> nodes = new HashMap<Integer, TileNode>();

	public IWeb(MethodContext ctx) {
		super(ctx);
	}

	public boolean load(String path) {
		// Could use serialization but I don't think it will work with RSBot & the SDN might break it

		DataInputStream in = null;
		try {
			in = new DataInputStream(new FileInputStream(path));

			// Header
			int version = in.readInt(); // could be useful
			int linkCount = in.readInt();
			int nodeCount = in.readInt();

			final RequirementType[] requirementTypes = RequirementType.values(); // Cache this

			// Data

			// Links
			for (int i = 0; i < linkCount; i++) {
				int id = in.readInt();
				int startNodeId = in.readInt();
				int endNodeId = in.readInt();
				double cost = in.readDouble();

				Link link;

				// Link
				if (links.containsKey(id)) {
					link = links.get(id); // Can load web twice
				} else {
					link = new Link(ctx, id, startNodeId, endNodeId, cost);
					links.put(id, link);
				}

				// Start
				if (nodes.containsKey(startNodeId)) {
					link.setStartNode(nodes.get(startNodeId));
				} else {
					TileNode node = new TileNode(startNodeId);
					nodes.put(startNodeId, node);
					link.setStartNode(node);
				}

				// End
				if (nodes.containsKey(endNodeId)) {
					link.setEndNode(nodes.get(endNodeId));
				} else {
					TileNode node = new TileNode(endNodeId);
					nodes.put(endNodeId, node);
					link.setEndNode(node);
				}

				// Requirements
				int requirementCount = in.readInt();
				for (int j = 0; j < requirementCount; j++) {
					RequirementType requirementType = requirementTypes[in.readInt()];
					switch (requirementType) {
						case NONE:
							break;
						case LEVEL:
							link.addRequirement(new LevelRequirement(in.readInt(), in.readInt())); // could save memory by seeing if the same requirement is already loaded
							break;
					}
					// Some of these will probably have to be added manually in code
				}
			}

			// Nodes
			for (int i = 0; i < nodeCount; i++) {
				int id = in.readInt();

				TileNode node;
				if (nodes.containsKey(id)) {
					node = nodes.get(id);
				} else {
					node = new TileNode(id);
					nodes.put(id, node);
				}

				node.setLocation(new Tile(in.readInt(), in.readInt(), in.readInt()));

				// Add neighbours to node
				int linkedLinkCount = in.readInt();
				for (int j = 0; j < linkedLinkCount; j++) {
					int linkId = in.readInt();
					if (links.containsKey(linkId)) {
						node.add(links.get(linkId));
					} else {
						throw new Exception("Link " + linkId + " is missing, wanted by Node " + id);
					}
				}
			}

			// TODO: Nearest-neighbour, depends how many nodes there are kd-tree would work well
			// http://www.thechaithanya.com/2012/02/nearest-neighbour-search-using-kdtree_28.html
			// http://geom-java.sourceforge.net/demos/nearestNeighborKDTreeDemo.html

			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ignored) {
			}
		}

		return false;
	}
}
