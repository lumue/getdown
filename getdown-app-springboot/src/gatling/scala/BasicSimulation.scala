import io.gatling.core.Predef._
import io.gatling.core.feeder.RecordSeqFeederBuilder
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
class BasicSimulation extends Simulation {
//	val app: ConfigurableApplicationContext = SpringApplication.run(classOf[Application])
//	Runtime.getRuntime.addShutdownHook(new Thread() {
//		override def run(): Unit = app.stop()
//	})


	val feeder: RecordSeqFeederBuilder[String] = csv("xhamster-urls.csv")

	val httpConf: HttpProtocolBuilder = http.baseURL("http://localhost:8001")


	val scn: ScenarioBuilder = scenario("BasicSimulation")
		  .repeat(14) {
			  feed(feeder)
			    .exec(
				  http("add ${url}").post("/download").body(StringBody("${url}"))
			  )
			  .pause(1)
		  }


	setUp(
		scn.inject(atOnceUsers(2))
	).protocols(httpConf)
}