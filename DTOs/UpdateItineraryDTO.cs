using System.ComponentModel.DataAnnotations;

namespace CultureXAPI.DTOs
{
    public class UpdateItineraryDTO
    {

        [Required]
        public string Title { get; set; }

        public string? Description { get; set; }

        public DateTime? StartDate { get; set; }

        public DateTime? EndDate { get; set; }

        public string[]? Activities { get; set; }

    }
}
