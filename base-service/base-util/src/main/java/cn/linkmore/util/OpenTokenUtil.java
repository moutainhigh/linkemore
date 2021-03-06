package cn.linkmore.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

public class OpenTokenUtil {

	//static String Secret = "8c563ca518f74433a631e8f6c3077f91";
	static String Secret = "123456789123456789";
	private static final long EXPIRE_TIME = 48 * 60 * 60 * 1000;

	public static String createToken() throws Exception {
		long now = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(new Date()));
		Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
		System.out.println(sdf.format(date));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("alg", "HS256");
		map.put("typ", "JWT");
		String token = JWT.create().withHeader(map)
				.withClaim("uid", "linkmore2019")
				.withClaim("mobile", "18514410532")
			    .withClaim("plates", "[京M92977,京Z63692]").withExpiresAt(date).withIssuedAt(new Date(now))
				.sign(Algorithm.HMAC256(Secret));
		return token;
	}
	
	public static String createLdToken() throws Exception {
		long now = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(new Date()));
		Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
		System.out.println(sdf.format(date));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("alg", "HS256");
		map.put("typ", "JWT");
		String token = JWT.create().withHeader(map)
				.withClaim("uid", "linkmore2018")
				.withClaim("mobile", "18514410532")
			    .withExpiresAt(date).withIssuedAt(new Date(now))
				.sign(Algorithm.HMAC256(Secret));
		return token;
	}

	// 解析token
	public static Map<String, Claim> verifyToken(String token, String secret) throws Exception {

		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();

		DecodedJWT jwt = verifier.verify(token);

		return jwt.getClaims();

	}

	public static void main(String[] args) {
		//上海德比
		/*try {
			String token = createToken();
			System.out.println(token);
			Map<String, Claim> map = verifyToken(token, Secret);
			System.out.println(map.get("uid").asString());
			System.out.println(map.get("mobile").asString());
			System.out.println(map.get("plates").asString());
			String plates = map.get("plates").asString();
			if (plates.startsWith("[")) {
				plates = plates.substring(1);
	        }
	        if (plates.endsWith("]")) {
	        	plates = plates.substring(0,plates.length() - 1);
	        }
	        if(plates.length()>0) {
	        	String[] plateArr = plates.split(",");
	        	for(String plate : plateArr) {
	        		System.out.println(plate);
	        	}
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		//北京蓝黛
		try {
			String token = createLdToken();
			System.out.println(token);
			Map<String, Claim> map = verifyToken(token, Secret);
			System.out.println(map.get("uid").asString());
			System.out.println(map.get("mobile").asString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
