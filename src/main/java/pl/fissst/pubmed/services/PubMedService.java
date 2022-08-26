package pl.fissst.pubmed.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import pl.fissst.pubmed.dto.*;
import pl.fissst.pubmed.dto.efetch.EFetch;
import pl.fissst.pubmed.dto.esearch.ESearch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
    public class PubMedService {

    private final static Logger LOG = LoggerFactory.getLogger(PubMedService.class);

    private final String SCHEME = "https";
    private final String HOST = "eutils.ncbi.nlm.nih.gov";
    private final String EFETCH = "/entrez/eutils/efetch.fcgi";
    private final String ESEARCH = "/entrez/eutils/esearch.fcgi";
    private final String RETMODE="retmode";
    private final String RETMODE_XML="xml";
    private final String DB="db";
    private final String DB_PUBMED="pubmed";
    private final String ID="id";
    private final String RETSTART="retstart";
    private final String RETMAX="retmax";
    private final String DATETYPE="datetype";
    private final String DATETYPE_PDAT="pdat";
    private final String MINDATE="mindate";
    private final String MAXDATE="maxdate";
    private final String API_KEY="api_key";
    private  final String TERM="term";
    private static final String TERM_VALUE = "((%s[Text+Word])+OR+(%s[title]))+AND+(*%s*[filter])+AND+(%s[Language])+AND+(*%s*[Affiliation])";

    private final String COUNTRY="Switzerland";
    private final String LANGUAGE="English";
    private final String ORGANISM="human";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private final RestTemplate restTemplate;

    private final Integer RETMAX_DEFAULT=10000;
    private final Integer RETSTART_DEFAULT=0;
    private final SortType SORT=SortType.COUNT;
    private final OrderType ORDER=OrderType.DESC;


    private final String API_KEY_VALUE;

    public PubMedService(RestTemplateBuilder builder,@Value("${pubmed.api_key}") String api_key_value) {
        this.restTemplate = builder.build();
        this.API_KEY_VALUE = api_key_value;
    }

    public PubMedAuthors getAuthorsPaginated(String disease, Integer size, Integer page, SortType sort, OrderType order){
        PubMedAuthors pubMedAuthors = PubMedAuthors.builder()
                .order(order)
                .sort(sort)
                .requestedPage(page)
                .perPage(size)
                .authors(new ArrayList<>())
                .build();
        getNamesByUIDS(getUIDS(getDisease(disease),pubMedAuthors),pubMedAuthors);
        sort(pubMedAuthors);
        return pubMedAuthors;
    }

    public List<PubMedAuthor> getAllAuthors(String disease){
        PubMedAuthors pubMedAuthors = PubMedAuthors.builder()
                .order(ORDER)
                .sort(SORT)
                .requestedPage(RETSTART_DEFAULT)
                .perPage(RETMAX_DEFAULT)
                .authors(new ArrayList<>())
                .build();
        getNamesByUIDS(getUIDS(getDisease(disease),pubMedAuthors),pubMedAuthors);
        for (Integer i=1; i<=pubMedAuthors.getPagesCount();i++){
            pubMedAuthors.setRequestedPage(i);
            getNamesByUIDS(getUIDS(getDisease(disease),pubMedAuthors),pubMedAuthors);
        }
        sort(pubMedAuthors);
        return pubMedAuthors.getAuthors();
    }

    public Map<String,ComputingInfo> getInfo(List<String> diseases){
        Map<String,ComputingInfo> infoMap = new HashMap<>();
        for (String disease:diseases) {
            ComputingInfo info = ComputingInfo.builder()
                    .articleCount(0L)
                    .authorsCount(0L)
                    .time(0L)
                    .build();
            long befor=System.currentTimeMillis();
            PubMedAuthors pubMedAuthors = PubMedAuthors.builder()
                    .order(ORDER)
                    .sort(SORT)
                    .requestedPage(RETSTART_DEFAULT)
                    .perPage(RETMAX_DEFAULT)
                    .authors(new ArrayList<>())
                    .build();

            List<String> uids = new ArrayList<>();
            uids.addAll(getUIDS(getDisease(disease),pubMedAuthors));
            info.setArticleCount(info.getArticleCount()+uids.size());
            getNamesByUIDS(uids,pubMedAuthors);
            uids.clear();

            for (Integer i=1; i<=pubMedAuthors.getPagesCount();i++){
                pubMedAuthors.setRequestedPage(i);
                uids.addAll(getUIDS(getDisease(disease),pubMedAuthors));
                info.setArticleCount(info.getArticleCount()+uids.size());
                getNamesByUIDS(uids,pubMedAuthors);
                uids.clear();
            }
            sort(pubMedAuthors);
            info.setAuthorsCount((long)pubMedAuthors.getAuthors().size());
            long after=System.currentTimeMillis();
            info.setTime(after-befor);
            infoMap.put(getDisease(disease),info);
        }

        return infoMap;
    }

    private List<String> getUIDS(String disease, PubMedAuthors medAuthors) {
        MultiValueMap<String,String> queryParams=new LinkedMultiValueMap<>(){{
            add(RETMODE,RETMODE_XML);
            add(API_KEY,API_KEY_VALUE);
            add(DATETYPE,DATETYPE_PDAT);
            add(MINDATE,LocalDate.now().minusYears(10).format(formatter));
            add(MAXDATE,LocalDate.now().format(formatter));
            add(TERM, String.format(TERM_VALUE,disease,disease,ORGANISM,LANGUAGE,COUNTRY));
            add(DB,DB_PUBMED);
            add(RETMAX,medAuthors.getPerPage().toString());
            add(RETSTART,medAuthors.getRequestedPage().toString());
        }};

        UriComponents eSearchUrl = UriComponentsBuilder.newInstance()
                .scheme(SCHEME).host(HOST)
                .path(ESEARCH).queryParams(queryParams).build();
        ESearch eSearch = restTemplate.getForObject(eSearchUrl.toString(),ESearch.class);

        medAuthors.setPagesCount(getPages(medAuthors.getPerPage(),eSearch.getCount()));

        if(eSearch.getIdList().getIds()!=null) {
            return eSearch.getIdList().getIds();
        }

        return null;
    }


    private PubMedAuthors getNamesByUIDS(List<String> uids,PubMedAuthors pubMedAuthors){
        Map<String,Integer> nameIndex = new HashMap<>();
        String uidsConnected=getConnectedUIDS(uids);
        if(uidsConnected!=null){
            MultiValueMap<String,String> queryParams=new LinkedMultiValueMap<>(){{
                add(RETMODE,RETMODE_XML);
                add(API_KEY,API_KEY_VALUE);
                add(DB,DB_PUBMED);
                add(ID,uidsConnected);
                add(RETMAX,pubMedAuthors.getPerPage().toString());
                add(RETSTART,pubMedAuthors.getRequestedPage().toString());
            }};

            UriComponents efetchUrl = UriComponentsBuilder.newInstance()
                    .scheme(SCHEME).host(HOST)
                    .path(EFETCH).build();

            EFetch eFetch = restTemplate
                    .postForObject(efetchUrl.toString(),queryParams, EFetch.class);

            eFetch.getPubmedArticle().forEach(pubmedArticle->{
                        pubmedArticle.getMedlineCitation().getArticle().getAuthorList().getAuthor()
                                .forEach(author -> {
                                            if(author.getAffiliationInfo()!=null && author.getAffiliationInfo().getAffiliation()!=null
                                                    &&
                                                    author.getForeName()!=null && author.getLastName()!=null
                                                    &&
                                                    author.getAffiliationInfo().getAffiliation().contains(COUNTRY)
                                                    &&
                                                    author.getLastName().length()>1 && author.getForeName().length()>1){
                                                    String name= author.getForeName()+" "+author.getLastName();
                                                    pubMedAuthors.setAuthorAndIncrement(name,nameIndex.get(name));
                                                    if(nameIndex.get(name)==null) {
                                                        nameIndex.put(name,pubMedAuthors.getAuthors().size()-1);
                                                    }
                                            }
                                        }
                                );
                    });
        }
        return pubMedAuthors;
    }

    private String getDisease(String disease){
        return disease.strip().replace(' ','+');
    }

    private Integer getPages(Integer perPage,Integer count){
        return (count/perPage+(count%perPage==0 ? 0 : 1))-1;
    }

    private String getConnectedUIDS(List<String> uids){
        if(uids==null){
            return null;
        }
        StringBuilder uidsString = new StringBuilder();
        uids.forEach(x-> uidsString.append(x.toString()+","));
        Integer index=uidsString.lastIndexOf(",");
        if(index!=-1)
            uidsString.deleteCharAt(index);
        return uidsString.toString();
    }

    private void sort(PubMedAuthors pubMedAuthors){
        if(pubMedAuthors.getSort()==SortType.COUNT){
            if(pubMedAuthors.getOrder()==OrderType.ASC){
                pubMedAuthors.getAuthors().sort(Comparator.comparing(PubMedAuthor::getCount));
            }else{
                pubMedAuthors.getAuthors().sort(Comparator.comparing(PubMedAuthor::getCount).reversed());
            }
        }else {
            if(pubMedAuthors.getOrder()==OrderType.ASC){
                pubMedAuthors.getAuthors().sort(Comparator.comparing(PubMedAuthor::getName));
            }else{
                pubMedAuthors.getAuthors().sort(Comparator.comparing(PubMedAuthor::getName).reversed());
            }
        }
    }




}

