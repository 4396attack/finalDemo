package album.yyj.zust.aiface;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("album.yyj.zust.aiface.mapper")
@SpringBootApplication()
public class AifaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AifaceApplication.class, args);
	}

}
