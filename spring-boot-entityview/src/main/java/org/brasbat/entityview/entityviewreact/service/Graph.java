package org.brasbat.entityview.entityviewreact.service;

import java.util.Collection;

import lombok.Data;

@Data
public class Graph {
	private final Collection<Node> nodes;
	private final Collection<Edge> edges;
}
