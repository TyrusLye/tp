package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.*;
import seedu.address.model.tag.Tag;


/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {


    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG,
                        PREFIX_ANIMAL_NAME, PREFIX_AVAILABILITY, PREFIX_ANIMAL_TYPE);


        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        if (argMultimap.getValue(PREFIX_ANIMAL_TYPE).isPresent() || argMultimap.getValue(PREFIX_ANIMAL_NAME).isPresent()) {
            if (!argMultimap.getValue(PREFIX_AVAILABILITY).isPresent()) {
                throw new ParseException("Availability is required when providing animalName or animalType.");
            }
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Optional<Name> animalName;
        if (argMultimap.getValue(PREFIX_ANIMAL_NAME).isPresent()) {
            animalName = Optional.of(ParserUtil.parseName(argMultimap.getValue(PREFIX_ANIMAL_NAME).get()));
        } else {
            animalName = Optional.empty();
        }

        Optional<Availability> availability;
        if (argMultimap.getValue(PREFIX_AVAILABILITY).isPresent()) {
            availability = Optional.of(ParserUtil.parseAvailability(argMultimap.getValue(PREFIX_AVAILABILITY).get()));
        } else {
            availability = Optional.empty();
        }

        Optional<AnimalType> animalType;
        String availabilityValue = argMultimap.getValue(PREFIX_AVAILABILITY).orElse("nil");
        if (argMultimap.getValue(PREFIX_ANIMAL_TYPE).isPresent()) {
            animalType = Optional.of(ParserUtil.parseAnimalType(
                    argMultimap.getValue(PREFIX_ANIMAL_TYPE).get(), availabilityValue));
        } else {
            animalType = Optional.empty();
        }

        Person person = new Person(name, phone, email, address, animalName, availability, animalType, tagList);

        return new AddCommand(person);
    }


    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }


}

