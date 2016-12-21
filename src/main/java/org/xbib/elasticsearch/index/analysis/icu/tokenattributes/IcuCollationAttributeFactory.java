package org.xbib.elasticsearch.index.analysis.icu.tokenattributes;

import com.ibm.icu.text.Collator;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeFactory;

/**
 * <p>
 *   Converts each token into its {@link com.ibm.icu.text.CollationKey} and
 *   then encodes bytes as an index term.
 * </p>
 * <p>
 *   <strong>WARNING:</strong> Make sure you use exactly the same Collator at
 *   index and query time -- CollationKeys are only comparable when produced by
 *   the same Collator.  {@link com.ibm.icu.text.RuleBasedCollator}s are
 *   independently versioned, so it is safe to search against stored
 *   CollationKeys if the following are exactly the same (best practice is
 *   to store this information with the index and check that they remain the
 *   same at query time):
 * </p>
 * <ol>
 *   <li>
 *     Collator version - see {@link Collator#getVersion()}
 *   </li>
 *   <li>
 *     The collation strength used - see {@link Collator#setStrength(int)}
 *   </li>
 * </ol>
 * <p>
 *   CollationKeys generated by ICU Collators are not compatible with those
 *   generated by java.text.Collators.  Specifically, if you use
 *   IcuCollationAttributeFactory to generate index terms, do not use
 *   {@link org.apache.lucene.collation.CollationAttributeFactory} on the query side, or vice versa.
 * </p>
 * <p>
 *   ICUCollationAttributeFactory is significantly faster and generates significantly
 *   shorter keys than CollationAttributeFactory.  See
 *   <a href="http://site.icu-project.org/charts/collation-icu4j-sun"
 *   >http://site.icu-project.org/charts/collation-icu4j-sun</a> for key
 *   generation timing and key length comparisons between ICU4J and
 *   java.text.Collator over several languages.
 * </p>
 */
public class IcuCollationAttributeFactory
        extends AttributeFactory.StaticImplementationAttributeFactory<IcuCollatedTermAttributeImpl> {
    private final Collator collator;

    /**
     * Create an ICUCollationAttributeFactory, using
     * {@link TokenStream#DEFAULT_TOKEN_ATTRIBUTE_FACTORY} as the
     * factory for all other attributes.
     * @param collator CollationKey generator
     */
    public IcuCollationAttributeFactory(Collator collator) {
        this(TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY, collator);
    }

    /**
     * Create an ICUCollationAttributeFactory, using the supplied Attribute
     * Factory as the factory for all other attributes.
     * @param delegate Attribute Factory
     * @param collator CollationKey generator
     */
    public IcuCollationAttributeFactory(AttributeFactory delegate, Collator collator) {
        super(delegate, IcuCollatedTermAttributeImpl.class);
        this.collator = collator;
    }

    @Override
    public IcuCollatedTermAttributeImpl createInstance() {
        return new IcuCollatedTermAttributeImpl(collator);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof IcuCollationAttributeFactory &&
                ((IcuCollationAttributeFactory) other).collator == collator;
    }

    @Override
    public int hashCode() {
        return collator.hashCode();
    }

}