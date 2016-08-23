/*
 * Netflix Eureka Client Plugin for Neo4j
 * Copyright (C) 2016  Balazs Brinkus
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.brinkus.lab.neo4j.eureka.unmanaged;

import com.brinkus.lab.neo4j.eureka.type.health.HealthCheck;
import com.brinkus.lab.neo4j.eureka.type.health.HealthStatusCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/health")
public class HealthCheckResource {

    private final GraphDatabaseService service;

    private final ObjectMapper mapper;

    public HealthCheckResource(@Context GraphDatabaseService service) {
        this.service = service;
        this.mapper = new ObjectMapper();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() throws JsonProcessingException {
        HealthCheck healthCheck = healthCheck();
        byte[] bytes = mapper.writeValueAsBytes(healthCheck);
        return Response.status(Response.Status.OK).entity(bytes).build();
    }

    public HealthCheck healthCheck() {
        try {
            if (check()) {
                return new HealthCheck(HealthStatusCode.UP, "Neo4j health check was success.");
            } else {
                return new HealthCheck(HealthStatusCode.DOWN, "Neo4j health check result was invalid!");
            }
        } catch (Exception e) {
            return new HealthCheck(HealthStatusCode.DOWN, "Neo4j health check failed!");
        }
    }

    private boolean check() {
        boolean hasEntry;
        try (Transaction tx = service.beginTx()) {
            Result result = service.execute("MATCH (n) RETURN n LIMIT 1");
            // TODO ugly hack
            hasEntry = result.resultAsString().contains("1 row");
            tx.success();
        }
        return hasEntry;
    }

}
