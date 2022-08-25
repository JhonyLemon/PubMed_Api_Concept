package pl.fissst.pubmed.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.fissst.pubmed.dto.efetch.EFetch;
import pl.fissst.pubmed.dto.egquery.EGQuery;
import pl.fissst.pubmed.dto.esearch.ESearch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
    public class PubMedService {

    private final String SCHEME = "https";
    private final String HOST = "eutils.ncbi.nlm.nih.gov";
    private final String EFETCH = "/entrez/eutils/efetch.fcgi";
    private final String ESEARCH = "/entrez/eutils/esearch.fcgi";
    private final String EGQUERY = "/gquery";

    private final String RETMODE="retmode";
    private final String RETMODE_XML="xml";
    private final String DB="db";
    private final String ID="id";
    private final String RETSTART="retstart";
    private final String RETMAX="retmax";
    private final String DATETYPE="datetype";
    private final String DATETYPE_PDAT="pdat";
    private final String MINDATE="mindate";
    private final String MAXDATE="maxdate";
    private final String API_KEY="api_key";
    private  final String TERM="term";
    private static final String TERM_VALUE = "((%s[Text+Word])+OR+(%s[title]))+AND+(*human*[filter])+AND+(English[Language])+AND+(*Switzerland*[Affiliation])";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private final RestTemplate restTemplate;
    private final Integer RETMAX_NUMBER=100;

    private final String API_KEY_VALUE;

    public PubMedService(RestTemplateBuilder builder,@Value("${pubmed.api_key}") String api_key_value) {
        this.restTemplate = builder.build();
        this.API_KEY_VALUE = api_key_value;
    }

    public Map<String,Integer> getDoctors(String disease){
        String query=disease.strip().replace(' ','+');
        return getAuthorsName(getArticlesUIDS(getDatabasesWithArticles(query),query));
    }


    private Map<String,Integer> getDatabasesWithArticles(String disease) {
        Map<String,Integer> dbs=new HashMap();
        UriComponents egQueryUrl = UriComponentsBuilder.newInstance()
                .scheme(SCHEME).host(HOST)
                .path(EGQUERY).queryParams(new LinkedMultiValueMap<>(){{
                    add(RETMODE,RETMODE_XML);
                    add(DATETYPE,DATETYPE_PDAT);
                    add(API_KEY,API_KEY_VALUE);
                    add(MINDATE,LocalDate.now().minusYears(10).format(formatter));
                    add(MINDATE,LocalDate.now().format(formatter));
                    add(TERM, String.format(TERM_VALUE,disease,disease));
                }}).build();
        EGQuery egQuery = restTemplate.getForObject(egQueryUrl.toString(), EGQuery.class);

        egQuery.getEgQueryResult().getResultItem().forEach(x->{
            if(x.getStatus().equals("Ok") && !x.getCount().equals("0"))
                dbs.put(x.getDbName(),Integer.valueOf(x.getCount()));
        });
        return dbs;
    }

    private Map<String,List<String>> getArticlesUIDS(Map<String,Integer> dbsAndCount,String disease) {
        Map<String,List<String>> uids = new HashMap<>();
        dbsAndCount.keySet().forEach(key->uids.put(key,new ArrayList<>()));

        MultiValueMap<String,String> queryParams=new LinkedMultiValueMap<>(){{
            add(RETMODE,RETMODE_XML);
            add(API_KEY,API_KEY_VALUE);
            add(DATETYPE,DATETYPE_PDAT);
            add(MINDATE,LocalDate.now().minusYears(10).format(formatter));
            add(MAXDATE,LocalDate.now().format(formatter));
            add(TERM, String.format(TERM_VALUE,disease,disease));
            add(DB,"");
            add(RETMAX,RETMAX_NUMBER.toString());
            add(RETSTART,"0");
        }};

        dbsAndCount.forEach((dbName,articleCount)->{
           for( Integer i=0;i<articleCount/RETMAX_NUMBER+(articleCount%RETMAX_NUMBER==0 ? 0 : 1); i++){
               queryParams.set(DB,dbName);
               queryParams.set(RETSTART,i.toString());
               UriComponents eSearchUrl = UriComponentsBuilder.newInstance()
                       .scheme(SCHEME).host(HOST)
                       .path(ESEARCH).queryParams(queryParams).build();
               ESearch eSearch = restTemplate.getForObject(eSearchUrl.toString(),ESearch.class);


               if(eSearch.getIdList().getIds()!=null) {
                   eSearch.getIdList().getIds().forEach(id -> {
                       uids.get(dbName).add(id);
                   });
               }
            }

        });
        return uids;
    }

    private Map<String,Integer> getAuthorsName(Map<String,List<String>> uidsAndDbs) {
        Map<String,Integer> authors = new HashMap<>();

        MultiValueMap<String,String> queryParams=new LinkedMultiValueMap<>(){{
            add(RETMODE,RETMODE_XML);
            add(API_KEY,API_KEY_VALUE);
            add(DB,"");
            add(ID,"");
            add(RETMAX,RETMAX_NUMBER.toString());
            add(RETSTART,"0");
        }};

        UriComponents efetchUrl = UriComponentsBuilder.newInstance()
                .scheme(SCHEME).host(HOST)
                .path(EFETCH).build();

        uidsAndDbs.forEach((dbName,uids)->{

            StringBuilder uidsString = new StringBuilder();
            uids.forEach(x-> uidsString.append(x.toString()+","));
            uidsString.deleteCharAt(uidsString.length()-1);

            queryParams.set(DB,dbName);
            queryParams.set(ID,uidsString.toString());

            for( Integer i=0;i<uids.size()/RETMAX_NUMBER+(uids.size()%RETMAX_NUMBER==0 ? 0 : 1); i++){
                queryParams.set(RETSTART,i.toString());

                EFetch eFetch = restTemplate
                    .postForObject(efetchUrl.toString(),queryParams, EFetch.class);

                eFetch.getPubmedArticle()
                        .forEach(pubmedArticle->{
                            pubmedArticle.getMedlineCitation()
                                    .getArticle()
                                    .getAuthorList()
                                    .getAuthor()
                                    .forEach(author -> {
                                                if(author.getAffiliationInfo()!=null
                                                        &&
                                                        author.getAffiliationInfo().getAffiliation()!=null
                                                        &&
                                                        author.getAffiliationInfo().getAffiliation().contains("Switzerland")
                                                        &&
                                                        author.getForeName()!=null
                                                        &&
                                                        author.getForeName().length()>1 && author.getLastName()!=null
                                                        &&
                                                        author.getLastName().length()>1){
                                                    String name= author.getForeName()+" "+author.getLastName();
                                                    if(authors.putIfAbsent(name,1)!=null){
                                                        authors.put(name,(authors.get(name)+1));
                                                    }
                                                }
                                            }
                                    );
                        });
            }

        });
        return authors;
    }

}

