package cz.cuni.mff.xrg.odcs.extractor.datanest;


import cz.cuni.mff.xrg.odcs.extractor.data.DatanestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HarvesterFactory {
    private final static Logger LOG = LoggerFactory.getLogger(HarvesterFactory.class);

    public static AbstractDatanestHarvester buildHarvester(DatanestType type) {
        AbstractDatanestHarvester harvester = null;

        switch (type) {
            case ORGANIZATION:
                LOG.info("create ORGANIZATION harvester");

                try {
                    harvester = new OrganizationsDatanestHarvester();
                } catch (Exception e) {
                    LOG.error("Problem", e);
                }
                break;
            case POLITICAL:
                LOG.info("create POLITICAL harvester");
                try {
                    harvester = new PoliticalPartyDonationsDatanestHarvester();
                } catch (Exception e) {
                    LOG.error("Problem", e);
                }
                break;
            case PROCUREMENT:
                LOG.info("create PROCUREMENT harvester");
                try {
                    harvester = new ProcurementsDatanestHarvester();
                } catch (Exception e) {
                    LOG.error("Problem", e);
                }
                break;
            default:
                LOG.info("DEFAULT");
                break;


        }
        return harvester;
    }
}
