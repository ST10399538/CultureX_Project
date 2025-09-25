namespace CultureXAPI.DTOs
{
    public class CulturalContentDTO
    {

        public Guid Id { get; set; }
        public Guid CountryId { get; set; }
        public Guid CategoryId { get; set; }
        public string Title { get; set; }
        public string? Content { get; set; }
        public string[]? Dos { get; set; }
        public string[]? Donts { get; set; }
        public string[]? Examples { get; set; }
        public string CountryName { get; set; }
        public string CategoryName { get; set; }

    }
}
