//usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.h2database:h2:1.4+
//DEPS org.hibernate:hibernate-core:5.4.10.Final
//DEPS org.jooq:jooq:3.12.3
//
// This example uses the Sakila database from https://dev.mysql.com/doc/sakila/en/ and in various formats
// at https://www.jooq.org/sakila. The h2 version comes from https://github.com/maxandersen/sakila-h2

import static java.lang.System.out;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.Transformers;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultConnectionProvider;
import org.jooq.impl.SQLDataType;


public class hibernate_friends {

    SessionFactory sf = null;

    SessionFactory setup() {
        if (sf == null) {
            sf = new Configuration().setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                    .setProperty("hibernate.connection.url", "jdbc:h2:./friends")
                    .setProperty("hibernate.connection.username", "sa")
                    .buildSessionFactory();
        }
        return sf;
    }

    void createDB(StatelessSession ss) {
        var ctx = DSL.using(new DefaultConfiguration()
        .set(new DefaultConnectionProvider(ss.connection()))
             .set(SQLDialect.H2)
             .set(new Settings().withExecuteLogging(false)));
        ctx.createTable("Person")
            .column("name", SQLDataType.VARCHAR(20))
            .column("town", SQLDataType.VARCHAR(20))
            .column("age", SQLDataType.INTEGER)
            .execute();
    }

    void run() {
        
        var db = setup().openStatelessSession();
        
        createDB(db);
        
        var sqlString = "SELECT first_name as firstName, last_name as lastName, count(*) films\n" +
                "FROM actor AS a\n" +
                "JOIN film_actor AS fa USING (actor_id)\n" +
                "GROUP BY a.actor_id, first_name, last_name\n" +
                "ORDER BY films DESC\n" +
                "LIMIT 10";


        db.createNativeQuery(sqlString)
        .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).stream().forEach(o -> out.println(o));

        //need other public classes which jbang can't compile/include at the moment.
        //db.createNativeQuery(sqlString).addScalar("firstName").addScalar("lastName").addScalar("films")
        //        .setResultTransformer(Transformers.aliasToBean(DTO.class)).stream().forEach(o -> out.println(o));

    }

    public static void main(String... args) {
        new hibernate_friends().run();
    }
}
