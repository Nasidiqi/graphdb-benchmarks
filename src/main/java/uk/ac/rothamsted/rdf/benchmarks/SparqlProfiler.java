package uk.ac.rothamsted.rdf.benchmarks;

import static java.lang.System.out;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.atlas.logging.Log;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVReaderHeaderAware;

import info.marcobrandizi.rdfutils.jena.SparqlUtils;
import uk.ac.ebi.utils.io.IOUtils;
import uk.ac.ebi.utils.time.XStopWatch;

/**
 * TODO: comment me!
 *
 * @author brandizi
 * <dl><dt>Date:</dt><dd>15 Aug 2018</dd></dl>
 *
 */
public class SparqlProfiler extends AbstractProfiler
{
	protected RDFConnection rdfConnection;
	
	public SparqlProfiler ( String basePath, String endPointUrl )
	{
		this ();
		this.basePath = basePath;
		this.endPointUrl = endPointUrl;
	}

	public SparqlProfiler ()
	{
		super ( "sparql" );
	}
	
	@Override
	protected long profileQuery ( String name )
	{
		String sparql = getQueryString ( name );
					
		if ( this.rdfConnection == null )
			this.rdfConnection = RDFConnectionFactory.connect ( endPointUrl );
		
		// Clock the query
		try
		{
			return XStopWatch.profile ( () -> 
			{
				Query query = QueryFactory.create ( sparql, Syntax.syntaxARQ );
				try ( QueryExecution qx = this.rdfConnection.query ( query ) )
				{
					ResultSet rs = qx.execSelect ();
					if ( rs.hasNext () ) rs.next ();
				}
			});
		}
		catch ( QueryException ex ) 
		{
			log.error ( "Error while parsing {}, query is:\n{}", name, sparql );
			throw new IllegalArgumentException ( "Error while parsing SPARQL '" + name + "': " + ex.getMessage (), ex ); 
		}
		
	}	
}
