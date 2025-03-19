package com.example.v2.swagController;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

//資源類
@Path("/example")
@Produces(MediaType.APPLICATION_JSON)
public class ExampleResource {

	@GET
	@Path("/{id}")
	@Operation(summary = "Get example by ID", responses = {
			@ApiResponse(responseCode = "200", description = "Successful retrieval", content = @Content(schema = @Schema(implementation = ExampleModel.class))),
			@ApiResponse(responseCode = "404", description = "Example not found") })
	public Response getExample(
			@Parameter(description = "ID of example to fetch", required = true) @PathParam("id") String id) {
		// 這裡應該是實際的業務邏輯
		ExampleModel example = new ExampleModel(id, "Example " + id);
		return Response.ok(example).build();
	}
}

//模型類
class ExampleModel {
	private String id;
	private String name;

	public ExampleModel(String id, String name) {
		this.id = id;
		this.name = name;
	}

	// Getters and setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}