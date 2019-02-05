class NotesController < ActionController::API
  def create
    return render json: { error: 'JSON expected' }, status: 406 unless request.content_type == 'application/json'

    note = Note.new(content: request.raw_post)
    note.save!
    render json: note, status: :created
  end

  def show
    note = Note.find(params.require(:id))
    render json: note
  end

  def index
    notes = Note.all
    render json: notes
  end

  def destroy
    deleted = Note.delete(params.require(:id))
    if deleted == 1
      head :no_content
    else
      head :not_found
    end
  end

  def update
    case request.content_type
    when 'application/json'
      note = Note.find(params.require(:id))
      note.update!(content: request.raw_post)
      render json: note

    when 'application/json-patch+json'
      note = Note.find(params.require(:id))
      patch = Hana::Patch.new(JSON.parse(request.raw_post))
      base = JSON.parse(note.content)
      result = JSON.generate(patch.apply(base))
      note.update!(content: result) if result != base
      render json: note

    else
      render json: { error: 'Expected JSON or JSON Patch payload' }, status: 406
    end
  end
end
