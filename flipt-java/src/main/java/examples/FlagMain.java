package examples;

import io.flipt.api.FliptClient;
import io.flipt.api.flags.Flag;
import io.flipt.api.flags.FlagException;
import io.flipt.api.flags.models.ListFlagsResponse;

public class FlagMain {
  public static void main(String[] args) throws FlagException {
    FliptClient fliptClient = FliptClient.builder().build();
    Flag flag = fliptClient.flags();

    ListFlagsResponse listFlagsResponse = flag.listFlags("default");
    System.out.println("Count: " + listFlagsResponse.getTotalCount());

    io.flipt.api.flags.models.Flag getFlagResponse = flag.getFlag("default", "create-flag");
    System.out.println(getFlagResponse.getKey() + " " + getFlagResponse.getName());
    System.out.println("Is enabled: " + getFlagResponse.isEnabled());
  }
}
