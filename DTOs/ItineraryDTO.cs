namespace CultureXAPI.DTOs
{
    public class ItineraryDTO
    {

        public Guid Id { get; set; }
        public Guid UserId { get; set; }
        public Guid CountryId { get; set; }
        public string Title { get; set; }
        public string? Description { get; set; }
        public DateTime? StartDate { get; set; }
        public DateTime? EndDate { get; set; }
        public string[]? Activities { get; set; }
        public string CountryName { get; set; }
        public DateTime CreatedAt { get; set; }
        public DateTime UpdatedAt { get; set; }

    }
}
