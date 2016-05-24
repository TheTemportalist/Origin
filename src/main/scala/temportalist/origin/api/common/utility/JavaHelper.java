package temportalist.origin.api.common.utility;

import scala.collection.Seq;

import java.util.Arrays;

/**
 * @author TheTemportalist
 */
public class JavaHelper {

	public static Seq<Object> seqFrom(Object... registers) {
		return scala.collection.JavaConversions.asScalaBuffer(Arrays.asList(registers));
	}

}
