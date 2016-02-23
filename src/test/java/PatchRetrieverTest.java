import com.xebialabs.patchformatter.PatchRetriever;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by sameer on 19/2/16.
 */
public class PatchRetrieverTest {

    @Test
    public void shouldFetchPrPatch() throws IOException {
        PatchRetriever retriever=new PatchRetriever();
        retriever.formatPatch(5);
    }
}
