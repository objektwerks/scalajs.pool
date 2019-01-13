package tripletail

import java.time.{LocalDate, LocalTime}

final case class Signup(email: String)

final case class Signin(license: String)

final case class Licensee(license: String, established: LocalDate, email: String)

final case class Pool(id: Integer, license: String, built: LocalDate, lat: Double, lon: Double, volume: Integer)

final case class Surface(id: Integer, poolId: Integer, installed: LocalDate, kind: String)

final case class Pump(id: Integer, poolId: Integer, installed: LocalDate, model: String)

final case class Timer(id: Integer, poolId: Integer, installed: LocalDate, model: String)

final case class TimerSetting(id: Integer, timerId: Integer, set: LocalDate, setOn: LocalTime, setOff: LocalTime)

final case class Heater(id: Integer, poolId: Integer, installed: LocalDate, model: String)

final case class HeaterOn(id: Integer, heaterId: Integer, temp: Integer, set: LocalDate)

final case class HeaterOff(id: Integer, heaterId: Integer, set: LocalDate)

final case class Cleaning(id: Integer, poolId: Integer, brush: Boolean, net: Boolean, vacuum: Boolean, skimmerBasket: Boolean,
                          pumpBasket: Boolean, pumpFilter: Boolean, pumpChlorineTablets: Integer, deck: Boolean)

final case class Measurement(id: Integer, poolId: Integer, temp: Integer, totalHardness: Integer, totalChlorine: Integer,
                             totalBromine: Integer, freeChlorine: Integer, ph: Double, totalAlkalinity: Integer, cyanuricAcid: Integer)

final case class Chemical(id: Integer, poolId: Integer, added: LocalDate, chemical: String, amount: Double, unit: String)

final case class Supply(id: Integer, poolId: Integer, purchased: LocalDate, cost: Double, item: String, amount: Double, unit: String)

final case class Repair(id: Integer, poolId: Integer, repaired: LocalDate, cost: Double, repair: String)