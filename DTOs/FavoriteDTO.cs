namespace CultureXAPI.DTOs
{
    public class FavoriteDTO
    {

        public Guid Id { get; set; }
        public Guid? CountryId { get; set; }
        public Guid? ContentId { get; set; }
        public string? CountryName { get; set; }
        public string? ContentTitle { get; set; }
        public string? CategoryName { get; set; }
        public DateTime CreatedAt { get; set; }

    }
}
