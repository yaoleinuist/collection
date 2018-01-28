

class SpecialPerson(val name: String)

class Student(val name : String)

class Older(val name : String)


implicit def object2Specialerson(obj: Object):SpecialPerson ={
  if(obj.getClass == classOf[Older]){
    val older = obj.asInstanceOf[Older]

    val name = older.name

    new SpecialPerson(name)
  }else{
    new SpecialPerson("None")
  }
}

var ticketNumber = 0
def buySpecialTicket(p: SpecialPerson) = {
  ticketNumber += 1
  "T-" + ticketNumber
}
buySpecialTicket(new Older("older"))

















