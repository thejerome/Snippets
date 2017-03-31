package dlc.servlet;

/**
 * $Id: distributedCDE.java,v 1.0 2003/06/10 
 * <br/>
 * Author: Ковальчук С.В.
 * <br/>
 * Структура для хранения параметров для вызова хранимых процедур
 */
public class RecParam {


 /**
  * <br> Название параметра =)
  */

public String name;

 /**
  * <br> Название типа параметра
  */

 public String type;
 /**
  * <br> Позиция параметра при вызове хранимой функции или процедуры
  */

 public int    position;

 /**
  * <br> Строка, содержащая в себе информация о том, является этот параметр
  * входным(IN), выходным (OUT) или одновременно и тем и другим(IN_OUT)
  */

 public String in_out;

 /**
  * <br> Сам параметр
  */

 public Object value;

		/** Конструктор
		 * @param name имя параметра
		 * @param type тип параметра
		 * @param position индекс параметра в списке для функции
		 * @param in_out входной, выходной или входной/выходной параметр
		 */
        public RecParam(String name, String type, int position, String in_out){
                this.name=name;
                this.type=type;
                this.position=position;
                this.in_out=in_out;
        }

		/** Конструктор копирования */
		public RecParam(RecParam src){
			name = src.name;
			type = src.type;
			position = src.position;
			in_out = src.in_out;
		}

}
