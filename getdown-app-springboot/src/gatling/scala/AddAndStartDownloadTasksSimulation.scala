import io.gatling.core.Predef._
import io.gatling.core.feeder.RecordSeqFeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class AddAndStartDownloadTasksSimulation extends Simulation {

	val httpConf: HttpProtocolBuilder = http.baseURL("http://localhost:8001")


	val scn: ScenarioBuilder = scenario("AddAndStartDownloadsFromFeederSimulation")
		.repeat(100, "count") {
			exec( session =>{
				session.set("username",
										session.get("username")
											.asOption[String]
											.getOrElse(java.util.UUID.randomUUID.toString))
			}
			).
			exec(
				http("add task")
					.post("/api/tasks")
					.body(StringBody("{\"content\":[{\"sourceUrl\":\"https://www.youtube.com/watch?v=nwP80FmSpOw\",\"targetLocation\":\"${username}-${count}\"}]}"))
					.asJSON
					.check(jsonPath("$.content[*].handle").findAll.saveAs("handles"))
			)
				.foreach("${handles}", "handle") {
					exec({
						http("start download")
							.post("/api/jobs")
							.asJSON
							.body(StringBody("{\"content\":[{\"handle\":\"${handle}\"}]}"))
					})
				}
		}


	setUp(
		scn.inject(atOnceUsers(10))
	).protocols(httpConf)
}