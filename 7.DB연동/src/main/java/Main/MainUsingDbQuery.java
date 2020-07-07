package Main;

import confing.DbConfig;
import confing.DbQueryConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


import dbquery.DbQuery;

public class MainUsingDbQuery {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class,
				DbQueryConfig.class);

		DbQuery dbQuery = ctx.getBean(DbQuery.class);
		int count = dbQuery.count();
		System.out.println(count);
		ctx.close();
	}
}
