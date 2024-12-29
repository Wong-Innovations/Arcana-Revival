package arcana.api.research.theorycraft;

public interface ITheorycraftAid {
    public Object getAidObject();
    public Class<? extends TheorycraftCard>[] getCards();
}
